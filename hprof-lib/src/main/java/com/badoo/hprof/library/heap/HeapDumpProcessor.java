package com.badoo.hprof.library.heap;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Callback interface to be used together with the HeapDumpReader. When HeapDumpReader.next() is called it will read the first byte
 * of the heap dump record (tag) and then call onHeapRecord().
 * <p/>
 * Created by Erik Andre on 16/07/2014.
 */
public interface HeapDumpProcessor {

    /**
     * Callback method invoked when the HeapProfReader has read a new record.
     *
     * @param tag The tag identifying the record (see HeapTag)
     * @param reader A reference to the reader being used
     * @throws IOException
     */
    void onHeapRecord(int tag, @Nonnull HeapDumpReader reader) throws IOException;

}
