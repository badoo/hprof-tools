package com.badoo.hprof.library.heap;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Erik Andre on 16/07/2014.
 */
public interface HeapDumpProcessor {

    void onHeapRecord(int tag, InputStream in) throws IOException;

}
