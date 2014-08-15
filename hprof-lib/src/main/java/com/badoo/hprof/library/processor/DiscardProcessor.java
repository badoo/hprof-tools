package com.badoo.hprof.library.processor;


import com.badoo.hprof.library.HprofProcessor;
import com.badoo.hprof.library.HprofReader;

import java.io.IOException;

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
    public void onRecord(int tag, int timestamp, int length, HprofReader reader) throws IOException {
        reader.getInputStream().skip(length);
    }
}
