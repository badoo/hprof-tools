package com.badoo.hprof.library.heap;

import java.io.IOException;

/**
 * Created by Erik Andre on 16/07/2014.
 */
public interface HeapDumpProcessor {

    void onHeapRecord(int tag, HeapDumpReader reader) throws IOException;

}
