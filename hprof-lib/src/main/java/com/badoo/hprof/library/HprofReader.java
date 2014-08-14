package com.badoo.hprof.library;

import java.io.IOException;
import java.io.InputStream;

import static com.badoo.hprof.library.IoUtil.readInt;

/**
 * Created by Erik Andre on 12/07/2014.
 */
public class HprofReader {

    private final InputStream in;
    private final HprofProcessor processor;
    private int readCount;

    public HprofReader(InputStream in, HprofProcessor processor) {
        this.in = in;
        this.processor = processor;
    }

    public boolean hasNext() throws IOException {
        return in.available() > 0;
    }

    public void next() throws IOException {
        if (readCount == 0) { // Read header first
            readHeader();
        } else {
            readRecord();
        }
        readCount++;
    }

    private void readRecord() throws IOException {
        int tagValue = in.read(); // 1 byte
        int time = readInt(in);
        int size = readInt(in);
        processor.onRecord(tagValue, time, size, in);
    }

    private void readHeader() throws IOException {
        String text = IoUtil.readNullTerminatedString(in);
        int idSize = readInt(in);
        int timeHigh = readInt(in);
        int timeLow = readInt(in);
        processor.onHeader(text, idSize, timeHigh, timeLow);
    }

}
