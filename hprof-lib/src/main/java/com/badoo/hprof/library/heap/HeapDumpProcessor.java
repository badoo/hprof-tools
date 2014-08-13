package com.badoo.hprof.library.heap;

import java.io.InputStream;

/**
 * Created by Erik Andre on 16/07/2014.
 */
public interface HeapDumpProcessor {

    void onRecord(int tag, InputStream in);

}
