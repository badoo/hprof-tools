package com.badoo.hprof.validator;

import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.Tag;
import com.badoo.hprof.library.heap.HeapDumpProcessor;
import com.badoo.hprof.library.heap.HeapDumpReader;
import com.badoo.hprof.library.heap.HeapTag;
import com.badoo.hprof.library.heap.processor.HeapDumpDiscardProcessor;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.processor.DiscardProcessor;
import com.badoo.hprof.validator.utils.Log;
import com.google.common.io.CountingInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Erik Andre on 13/12/14.
 */
public class ValidatingProcessor extends DiscardProcessor {

    private static final String TAG = "ValidatingProcessor";

    private final CountingInputStream in;
    private Map<Integer, String> strings = new HashMap<Integer, String>();
    private Map<Integer, ClassDefinition> classes = new HashMap<Integer, ClassDefinition>();
    private List<Instance> instances = new ArrayList<Instance>();

    public ValidatingProcessor(CountingInputStream in) {
        this.in = in;
    }

    @Override
    public void onHeader(String text, int idSize, int timeHigh, int timeLow) throws IOException {
        Log.d(TAG, "Header text=" + text + ", idSize=" + idSize + ", timeHigh=" + timeHigh + ", timeLow=" + timeLow);
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, HprofReader reader) throws IOException {
        //Log.d(TAG, "onRecord pos=" + in.getCount() + ", type=" + Tag.tagToString(tag));
        if (tag == Tag.STRING) {
            HprofString str = reader.readStringRecord(length, timestamp);
            strings.put(str.getId(), str.getValue());
        }
        else if (tag == Tag.LOAD_CLASS) {
            ClassDefinition cls = reader.readLoadClassRecord();
            classes.put(cls.getObjectId(), cls);
        }
        else if (tag == Tag.HEAP_DUMP || tag == Tag.HEAP_DUMP_SEGMENT) {
            readHeapDump(length);
        }
        else {
            super.onRecord(tag, timestamp, length, reader);
        }
    }

    private void readHeapDump(int length) throws IOException {
        HeapDumpProcessor processor = new ValidatingHeapDumpProcessor();
        HeapDumpReader reader = new HeapDumpReader(in, length, processor);
        while (reader.hasNext()) {
            reader.next();
        }
    }

    public void verifyClasses() {
        // Check the special case (java.lang.Class) needed to be preserved by MAT
        if (!strings.values().contains("java.lang.Class")) {
            throw new IllegalStateException("No string record of java.lang.Class");
        }
        // Verify names
        for (ClassDefinition cls : classes.values()) {
            if (!strings.containsKey(cls.getNameStringId())) {
                throw new IllegalStateException("No string with id " + cls.getNameStringId());
            }
        }
        // Verify class hierarchy
        for (ClassDefinition cls : classes.values()) {
            verifySuperClass(cls);
        }
    }

    private void verifySuperClass(ClassDefinition cls) {
        int superId = cls.getSuperClassObjectId();
        String className = strings.get(cls.getNameStringId());
        if (classes.containsKey(superId)) {
            verifySuperClass(classes.get(superId));
        }
        else if (!className.equals("java.lang.Object")) {
            Log.d(TAG, "Class " + className + " does not have a super class (" + superId + ")");
//            throw new IllegalStateException("Class " + className + " does not have a super class (" + superId + ")");
        }
    }

    private class ValidatingHeapDumpProcessor extends HeapDumpDiscardProcessor {

        @Override
        public void onHeapRecord(int tag, HeapDumpReader reader) throws IOException {
            //Log.d(TAG, "onHeapRecord pos=" + in.getCount() + ", type=" + HeapTag.tagToString(tag));
            if (tag == HeapTag.CLASS_DUMP) {
                reader.readClassDumpRecord(classes);
            }
            else if (tag == HeapTag.INSTANCE_DUMP) {
                instances.add(reader.readInstanceDump());
            }
            else {
                super.onHeapRecord(tag, reader);
            }
        }
    }
}
