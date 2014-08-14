package com.badoo.hprof.library.heap.processor;

import java.io.IOException;
import java.io.InputStream;

/**
 * A HeapDumpProcessor that reads each record and discards the data
 * <p/>
 * Created by Erik Andre on 14/08/2014.
 */
public class HeapDumpDiscardProcessor extends HeapDumpBaseProcessor {

    @Override
    public void onHeapRecord(int tag, InputStream in) throws IOException {
       skipHeapRecord(tag, in);
    }
}
