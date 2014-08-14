package com.badoo.hprof.library.heap.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A HeapDumpProcessor that reads each record and discards the data
 *
 * Created by Erik Andre on 14/08/2014.
 */
public class HeapDumpCopyProcessor extends HeapDumpBaseProcessor {

    private final OutputStream out;

    public HeapDumpCopyProcessor(OutputStream out) {
        this.out = out;
    }

    @Override
    public void onHeapRecord(int tag, InputStream in) throws IOException {
        copyHeapRecord(tag, in, out);
    }
}
