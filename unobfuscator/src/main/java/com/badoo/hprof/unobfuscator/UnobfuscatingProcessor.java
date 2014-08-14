package com.badoo.hprof.unobfuscator;

import com.badoo.hprof.library.Tag;
import com.badoo.hprof.library.heap.HeapDumpReader;
import com.badoo.hprof.library.heap.HeapTag;
import com.badoo.hprof.library.heap.processor.HeapDumpDiscardProcessor;
import com.badoo.hprof.library.processor.CopyProcessor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.badoo.hprof.library.IoUtil.copy;
import static com.badoo.hprof.library.IoUtil.readInt;
import static com.badoo.hprof.library.IoUtil.readString;
import static com.badoo.hprof.library.IoUtil.writeInt;

/**
 * Created by Erik Andre on 13/08/2014.
 */
public class UnobfuscatingProcessor extends CopyProcessor {

    private ClassDumpProcessor classDumpProcessor = new ClassDumpProcessor();
    private Map<Integer, String> strings = new HashMap<Integer, String>();

    public UnobfuscatingProcessor(OutputStream out) {
        super(out);
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, InputStream in) throws IOException {
        if (tag == Tag.HEAP_DUMP || tag == Tag.HEAP_DUMP_SEGMENT) {
            // Write the record to output but keep a copy to process
            byte[] record = new byte[length];
            in.read(record);
            writer.writeRecordHeader(tag, timestamp, length);
            out.write(record);
            readHeapDump(record);
        }
        else if (tag == Tag.STRING) {
            writer.writeRecordHeader(tag, timestamp, length);
            int stringId = readInt(in);
            writeInt(out, stringId);
            byte[] data = copy(in, out, length - 4);
            String string = new String(data);
            strings.put(stringId, string);
        }
        else {
            super.onRecord(tag, timestamp, length, in); // Discard
        }
    }

    private void readHeapDump(byte[] data) throws IOException {
        HeapDumpReader reader = new HeapDumpReader(new ByteArrayInputStream(data), data.length, classDumpProcessor);
        while (reader.hasNext()) {
            reader.next();
        }
    }

    class ClassDumpProcessor extends HeapDumpDiscardProcessor {

        @Override
        public void onHeapRecord(int tag, InputStream in) throws IOException {
//            if (tag == HeapTag.CLASS_DUMP) {
//
//            }
//            else {
                super.onHeapRecord(tag, in);
//            }
        }
    }


}
