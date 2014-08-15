package com.badoo.hprof.library;

import java.io.IOException;
import java.io.InputStream;

import static com.badoo.hprof.library.StreamUtil.readInt;

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

    /**
     * Returns true if there is more data to be read (You can call next())
     *
     * @return True if there is more data to be read.
     * @throws IOException
     */
    public boolean hasNext() throws IOException {
        return in.available() > 0;
    }

    /**
     * Read the next record. This will trigger a callback to the processor.
     *
     * @throws IOException
     */
    public void next() throws IOException {
        if (readCount == 0) { // Read header first
            readHeader();
        }
        else {
            readRecord();
        }
        readCount++;
    }

    public InputStream getInputStream() {
        return in;
    }

    private void readRecord() throws IOException {
        int tagValue = in.read(); // 1 byte tag, see definitions in Tag
        int time = readInt(in);
        int size = readInt(in);
        processor.onRecord(tagValue, time, size, this);
    }

    private void readHeader() throws IOException {
        String text = StreamUtil.readNullTerminatedString(in);
        int idSize = readInt(in);
        int timeHigh = readInt(in);
        int timeLow = readInt(in);
        processor.onHeader(text, idSize, timeHigh, timeLow);
    }

}
