package com.badoo.hprof.unobfuscator;

import com.badoo.hprof.library.HprofWriter;
import com.badoo.hprof.library.Tag;
import com.badoo.hprof.library.heap.HeapDumpReader;
import com.badoo.hprof.library.heap.HeapTag;
import com.badoo.hprof.library.heap.processor.HeapDumpCopyProcessor;
import com.badoo.hprof.library.heap.processor.HeapDumpDiscardProcessor;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.processor.CopyProcessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import static com.badoo.hprof.library.IoUtil.writeInt;

/**
 * Created by Erik Andre on 14/08/2014.
 */
public class StringUpdateProcessor extends CopyProcessor {

    private class ClassDefinitionRemoverProcessor extends HeapDumpCopyProcessor {

        public ClassDefinitionRemoverProcessor(OutputStream out) {
            super(out);
        }

        @Override
        public void onHeapRecord(int tag, InputStream in) throws IOException {
            if (tag == HeapTag.CLASS_DUMP) {
                HeapDumpDiscardProcessor.discard(tag, in); // Discard all class definitions since we are writing an updated version instead
            } else {
                super.onHeapRecord(tag, in);
            }
        }
    }

    private final Map<Integer, String> strings;
    private final Map<Integer, ClassDefinition> classes;
    private boolean writeUpdatedClassDefinitions = true;

    public StringUpdateProcessor(OutputStream out, Map<Integer, ClassDefinition> classes, Map<Integer, String> strings) {
        super(out);
        this.strings = strings;
        this.classes = classes;
    }

    @Override
    public void onHeader(String text, int idSize, int timeHigh, int timeLow) throws IOException {
        super.onHeader(text, idSize, timeHigh, timeLow);
        // Write all updated strings
        HprofWriter writer = new HprofWriter(out);
        for (Map.Entry<Integer, String> e : strings.entrySet()) {
            byte[] stringData = e.getValue().getBytes();
            writer.writeRecordHeader(Tag.STRING, 0, stringData.length + 4);
            writeInt(out, e.getKey()); // String id
            out.write(stringData);
        }
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, InputStream in) throws IOException {
        if (tag == Tag.STRING) {
            in.skip(length);
        } else if (tag == Tag.HEAP_DUMP || tag == Tag.HEAP_DUMP_SEGMENT) {
            if (writeUpdatedClassDefinitions) {
                // Write the updated class definitions before continuing with the existing data
                writeClasses(tag, timestamp);
                writeUpdatedClassDefinitions = false;
            }
            // Filter the heap dump, removing any class definition
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ClassDefinitionRemoverProcessor processor = new ClassDefinitionRemoverProcessor(buffer);
            HeapDumpReader reader = new HeapDumpReader(in, length, processor);
            while (reader.hasNext()) {
                reader.next();
            }
            byte[] data = buffer.toByteArray();
            writer.writeRecordHeader(tag, timestamp, data.length);
            out.write(data);
        }
        else {
            super.onRecord(tag, timestamp, length, in);
        }
    }

    private void writeClasses(int tag, int timestamp) throws IOException {
        // Write all class definitions to a buffer in order to calculate the size. Uses more memory but avoids an extra pass to calculate size before writing the data
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ClassDefinition cls : classes.values()) {
            cls.writeClassDump(buffer);
        }
        byte[] data = buffer.toByteArray();
        writer.writeRecordHeader(tag, timestamp, data.length);
        out.write(data);
    }
}
