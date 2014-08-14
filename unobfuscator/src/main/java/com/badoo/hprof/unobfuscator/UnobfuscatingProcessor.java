package com.badoo.hprof.unobfuscator;

import com.badoo.hprof.library.IoUtil;
import com.badoo.hprof.library.Tag;
import com.badoo.hprof.library.heap.HeapDumpProcessor;
import com.badoo.hprof.library.heap.HeapDumpReader;
import com.badoo.hprof.library.heap.processor.HeapDumpDiscardProcessor;
import com.badoo.hprof.library.processor.CopyProcessor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.badoo.hprof.library.IoUtil.copy;

/**
 * Created by Erik Andre on 13/08/2014.
 */
public class UnobfuscatingProcessor extends CopyProcessor {

    class ClassDumpProcessor extends HeapDumpDiscardProcessor {

        @Override
        public void onHeapRecord(int tag, InputStream in) throws IOException {
            super.onHeapRecord(tag, in);
            System.out.println("onHeapRecord: " + Integer.toHexString(tag));
        }
    }

    private ClassDumpProcessor classDumpProcessor = new ClassDumpProcessor();

    public UnobfuscatingProcessor(OutputStream out) {
        super(out);
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, InputStream in) throws IOException {
        System.out.println("onRecord: " + Integer.toHexString(tag));
        if (tag == Tag.HEAP_DUMP || tag == Tag.HEAP_DUMP_SEGMENT) {
            // Write the record to output but keep a copy to process
            byte[] record = new byte[length];
            in.read(record);
            writer.writeRecordHeader(tag, timestamp, length);
            out.write(record);
            readHeapDump(record);
        } else {
            super.onRecord(tag, timestamp, length, in); // Discard
        }
    }

    private void readHeapDump(byte[] data) throws IOException {
        HeapDumpReader reader = new HeapDumpReader(new ByteArrayInputStream(data), data.length, classDumpProcessor);
        while (reader.hasNext()) {
            reader.next();
        }
    }


}
