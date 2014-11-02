package com.badoo.bmd;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Erik Andre on 02/11/14.
 */
public class BmdReader extends DataReader {

    private final BmdProcessor processor;
    private boolean readHeader = true;

    public BmdReader(InputStream in, BmdProcessor processor) {
        super(in);
        this.processor = processor;
    }

    public InputStream getInputStream() {
        return in;
    }

    public boolean hasNext() throws IOException {
        return (in.available() > 0);
    }

    public void next() throws IOException {
        if (readHeader) {
            readHeader();
            readHeader = false;
        }
        else {
            readRecord();
        }
    }

    private void readRecord() throws IOException {
        int tag = readInt32();
        processor.onRecord(tag, this);
    }

    private void readHeader() throws IOException {
        int version = readInt32();
        int metaDataLength = readInt32();
        byte[] metadata = readRawBytes(metaDataLength);
        processor.onHeader(version, metadata);
    }

}
