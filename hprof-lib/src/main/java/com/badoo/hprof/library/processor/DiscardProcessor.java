package com.badoo.hprof.library.processor;


import com.badoo.hprof.library.HprofProcessor;
import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.util.StreamUtil;

import java.io.IOException;

import javax.annotation.Nonnull;

import static com.badoo.hprof.library.util.StreamUtil.*;

/**
 * A HprofProcessor implementation that reads and discards all records.
 * <p/>
 * Created by Erik Andre on 13/07/2014.
 */
public abstract class DiscardProcessor implements HprofProcessor {

    @Override
    public void onHeader(@Nonnull String text, int idSize, int timeHigh, int timeLow) throws IOException {
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, @Nonnull HprofReader reader) throws IOException {
        skip(reader.getInputStream(), length);
    }
}
