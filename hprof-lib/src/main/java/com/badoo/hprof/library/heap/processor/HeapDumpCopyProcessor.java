package com.badoo.hprof.library.heap.processor;

import com.badoo.hprof.library.heap.HeapDumpReader;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A HeapDumpProcessor that reads each record and discards the data
 * <p/>
 * Created by Erik Andre on 14/08/2014.
 */
public class HeapDumpCopyProcessor extends HeapDumpBaseProcessor {

    private final OutputStream out;

    public HeapDumpCopyProcessor(OutputStream out) {
        this.out = out;
    }

    @Override
    public void onHeapRecord(int tag, HeapDumpReader reader) throws IOException {
        copyHeapRecord(tag, reader.getInputStream(), out);
    }
}
