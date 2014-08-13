package com.badoo.hprof.library.processor;


import com.badoo.hprof.library.HprofProcessor;
import com.badoo.hprof.library.HprofWriter;
import com.badoo.hprof.library.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.badoo.hprof.library.IoUtil.copy;

/**
 * Created by Erik Andre on 13/07/2014.
 */
public class CopyProcessor implements HprofProcessor {

    private final HprofWriter writer;
    private final OutputStream out;

    public CopyProcessor(OutputStream out) {
        writer = new HprofWriter(out);
        this.out = out;
    }

    @Override
    public void onHeader(String text, int idSize, int timeHigh, int timeLow) throws IOException {
        writer.writeHeader(text, idSize, timeHigh, timeLow);
    }

    @Override
    public void onRecord(Tag tag, int timestamp, int length, InputStream in) throws IOException {
        writer.writeRecordHeader(tag, timestamp, length);
        copy(in, out, length);
    }
}
