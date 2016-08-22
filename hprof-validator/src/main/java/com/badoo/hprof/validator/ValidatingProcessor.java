package com.badoo.hprof.validator;

import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.Tag;
import com.badoo.hprof.library.heap.HeapDumpProcessor;
import com.badoo.hprof.library.heap.HeapDumpReader;
import com.badoo.hprof.library.heap.HeapTag;
import com.badoo.hprof.library.heap.processor.HeapDumpDiscardProcessor;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.model.ID;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.processor.DiscardProcessor;
import com.badoo.hprof.validator.utils.Log;
import com.google.common.io.CountingInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * A HPROF processor that performs simple verifications to check that the HPROF data is valid.
 * <p/>
 * Created by Erik Andre on 13/12/14.
 */
public class ValidatingProcessor extends DiscardProcessor {

    private static final String TAG = "ValidatingProcessor";

    private final CountingInputStream in;
    private Map<ID, String> strings = new HashMap<ID, String>();
    private Map<ID, ClassDefinition> classes = new HashMap<ID, ClassDefinition>();
    private List<Instance> instances = new ArrayList<Instance>();
    private Map<Instance, Long> instancePositions = new HashMap<Instance, Long>();

    public ValidatingProcessor(CountingInputStream in) {
        this.in = in;
    }

    @Override
    public void onHeader(@Nonnull String text, int idSize, int timeHigh, int timeLow) throws IOException {
        Log.d(TAG, "Header text=" + text + ", idSize=" + idSize + ", timeHigh=" + timeHigh + ", timeLow=" + timeLow);
        if (idSize != 4) {
            throw new RuntimeException("hprof validator is not implemented for idSize = " + idSize);
        }
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, @Nonnull HprofReader reader) throws IOException {
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

    /**
     * Verify that the loaded instance dumps conform with the class definition.
     */
    public void verifyInstances() {
        for (Instance instance : instances) {
            ClassDefinition cls = classes.get(instance.getClassId());
            // Check that the class exists
            if (cls == null) {
                throw new IllegalStateException("Object with id " + cls.getObjectId() + " does not have a class (" + instance.getClassId());
            }
            String name = strings.get(cls.getNameStringId());
            // Verify that the instance dump size is correct
            int expectedSize = getInstanceSize(cls);
            if (expectedSize != instance.getInstanceFieldData().length) {
                long location = instancePositions.get(instance);
                throw new IllegalStateException("Object with name " + name + " at " + location + " has mismatching instance size. Expected "
                    + expectedSize + " was " + instance.getInstanceFieldData().length);
            }
        }
    }

    /**
     * Verify that the class inheritance hierarchy is correct and that the name for each class is defined.
     */
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

    private void readHeapDump(int length) throws IOException {
        HeapDumpProcessor processor = new ValidatingHeapDumpProcessor();
        HeapDumpReader reader = new HeapDumpReader(in, length, processor);
        while (reader.hasNext()) {
            reader.next();
        }
    }

    private void verifySuperClass(ClassDefinition cls) {
        final ID superId = cls.getSuperClassObjectId();
        String className = strings.get(cls.getNameStringId());
        if (classes.containsKey(superId)) {
            verifySuperClass(classes.get(superId));
        }
        else if (superId.toLong() != 0) { // Zero is valid for classes that has not super class (like java.lang.Object and others)
            throw new IllegalStateException("Class " + className + " does not have a super class (" + superId + ")");
        }
    }

    private class ValidatingHeapDumpProcessor extends HeapDumpDiscardProcessor {

        @Override
        public void onHeapRecord(int tag, @Nonnull HeapDumpReader reader) throws IOException {
            //Log.d(TAG, "onHeapRecord pos=" + in.getCount() + ", type=" + HeapTag.tagToString(tag));
            if (tag == HeapTag.CLASS_DUMP) {
                reader.readClassDumpRecord(classes);
            }
            else if (tag == HeapTag.INSTANCE_DUMP) {
                long position = in.getCount();
                Instance instance = reader.readInstanceDump();
                instances.add(instance);
                instancePositions.put(instance, position);
            }
            else {
                super.onHeapRecord(tag, reader);
            }
        }
    }

    private int getInstanceSize(ClassDefinition cls) {
        int size = 0;
        while (cls != null) {
            size += cls.getInstanceSize();
            cls = classes.get(cls.getSuperClassObjectId());
        }
        return size;
    }
}
