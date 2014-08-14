package com.badoo.hprof.library;

import java.io.IOException;
import java.io.OutputStream;

import static com.badoo.hprof.library.IoUtil.writeInt;
import static com.badoo.hprof.library.IoUtil.writeNullTerminatedString;

/**
 * Created by Erik Andre on 13/07/2014.
 */
public class HprofWriter {

    private final OutputStream out;

    public HprofWriter(OutputStream out) {
        this.out = out;
    }

    public void writeHeader(String text, int idSize, int timeHigh, int timeLow) throws IOException {
        writeNullTerminatedString(out, text);
        writeInt(out, idSize);
        writeInt(out, timeHigh);
        writeInt(out, timeLow);
    }

    public void writeRecordHeader(int tag, int timestamp, int length) throws IOException {
        out.write(tag);
        writeInt(out, timestamp);
        writeInt(out, length);
    }

}
