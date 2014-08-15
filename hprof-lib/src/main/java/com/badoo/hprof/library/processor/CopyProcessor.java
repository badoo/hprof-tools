package com.badoo.hprof.library.processor;


import com.badoo.hprof.library.HprofProcessor;
import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.HprofWriter;

import java.io.IOException;
import java.io.OutputStream;

import static com.badoo.hprof.library.StreamUtil.copy;

/**
 * Created by Erik Andre on 13/07/2014.
 */
public class CopyProcessor implements HprofProcessor {

    protected final HprofWriter writer;
    protected final OutputStream out;

    public CopyProcessor(OutputStream out) {
        writer = new HprofWriter(out);
        this.out = out;
    }

    @Override
    public void onHeader(String text, int idSize, int timeHigh, int timeLow) throws IOException {
        writer.writeHprofFileHeader(text, idSize, timeHigh, timeLow);
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, HprofReader reader) throws IOException {
        writer.writeRecordHeader(tag, timestamp, length);
        copy(reader.getInputStream(), out, length);
    }
}
