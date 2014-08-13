package com.badoo.hprof.library.heap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Erik Andre on 16/07/2014.
 */
public class HeapDumpReader {

    private final InputStream in;
    private final HeapDumpProcessor processor;

    public HeapDumpReader(InputStream in, int size, HeapDumpProcessor processor) throws IOException {
        byte[] data = new byte[size];
        in.read(data);
        this.in = new ByteArrayInputStream(data);
        this.processor = processor;
    }

    public boolean hasNext() throws IOException {
        return in.available() > 0;
    }

    public void next() throws IOException {
        int tag = in.read();

    }
}
