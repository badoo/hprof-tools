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
public class DiscardProcessor implements HprofProcessor {

    public DiscardProcessor() {
    }

    @Override
    public void onHeader(String text, int idSize, int timeHigh, int timeLow) throws IOException {
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, InputStream in) throws IOException {
        in.skip(length);
    }
}
