package com.badoo.hprof.validator;

import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.Tag;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.processor.DiscardProcessor;
import com.badoo.hprof.validator.utils.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Erik Andre on 13/12/14.
 */
public class ValidatingProcessor extends DiscardProcessor {

    private static final String TAG = "ValidatingProcessor";

    private Map<Integer, String> strings = new HashMap<Integer, String>();
    private Map<Integer, ClassDefinition> classes = new HashMap<Integer, ClassDefinition>();

    @Override
    public void onHeader(String text, int idSize, int timeHigh, int timeLow) throws IOException {
        Log.d(TAG, "Header text=" + text + ", idSize=" + idSize + ", timeHigh=" + timeHigh + ", timeLow=" + timeLow);
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, HprofReader reader) throws IOException {
        if (tag == Tag.STRING) {
            HprofString str = reader.readStringRecord(length, timestamp);
            strings.put(str.getId(), str.getValue());
        }
        else if (tag == Tag.LOAD_CLASS) {
            ClassDefinition cls = reader.readLoadClassRecord();
            classes.put(cls.getObjectId(), cls);
        }
        else {
            super.onRecord(tag, timestamp, length, reader);
        }
    }

    public void verifyClasses() {
        // Check the special case (java.lang.Class) needed to be preserved by MAT
        if (!strings.values().contains("java.lang.Class")) {
            throw new IllegalStateException("No string record of java.lang.Class");
        }
        for (ClassDefinition cls : classes.values()) {
            if (!strings.containsKey(cls.getNameStringId())) {
                throw new IllegalStateException("No string with id " + cls.getNameStringId());
            }
        }
    }
}
