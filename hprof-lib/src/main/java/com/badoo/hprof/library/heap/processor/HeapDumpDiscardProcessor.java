package com.badoo.hprof.library.heap.processor;

import com.badoo.hprof.library.heap.HeapDumpReader;

import java.io.IOException;

/**
 * A HeapDumpProcessor that reads each record and discards the data
 * <p/>
 * Created by Erik Andre on 14/08/2014.
 */
public class HeapDumpDiscardProcessor extends HeapDumpBaseProcessor {

    @Override
    public void onHeapRecord(int tag, HeapDumpReader reader) throws IOException {
        skipHeapRecord(tag, reader.getInputStream());
    }
}
