package com.badoo.hprof.deobfuscator;

import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.Tag;
import com.badoo.hprof.library.heap.HeapDumpReader;
import com.badoo.hprof.library.heap.HeapTag;
import com.badoo.hprof.library.heap.processor.HeapDumpDiscardProcessor;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.model.ID;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.library.model.NamedField;
import com.badoo.hprof.library.model.StackFrame;
import com.badoo.hprof.library.model.StaticField;
import com.badoo.hprof.library.processor.DiscardProcessor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.annotation.Nonnull;

import static com.badoo.hprof.library.util.StreamUtil.read;
import static com.badoo.hprof.library.util.StreamUtil.readID;
import static com.badoo.hprof.library.util.StreamUtil.readInt;

/**
 * Created by Erik Andre on 13/08/2014.
 */
public class DataCollectionProcessor extends DiscardProcessor {

    private ClassDumpProcessor classDumpProcessor = new ClassDumpProcessor();
    private Map<ID, HprofString> strings = new HashMap<ID, HprofString>();
    private Map<ID, ClassDefinition> classes = new HashMap<ID, ClassDefinition>();
    private Map<Integer, ClassDefinition> classesWithSerialNumbers = new HashMap<Integer, ClassDefinition>();
    private Map<ID, StackFrame> stackFrames = new HashMap<ID, StackFrame>();
    private Set<ID> referencedStringIds = new HashSet<ID>();
    private ID lastStringId = new ID();



    class ClassDumpProcessor extends HeapDumpDiscardProcessor {

        @Override
        public void onHeapRecord(int tag, @Nonnull HeapDumpReader reader) throws IOException {
            if (tag == HeapTag.CLASS_DUMP) {
                ClassDefinition cls = reader.readClassDumpRecord(classes);
                // Since the names of obfuscated fields are shared between classes we need to deduplicate the references, otherwise we cannot deobfuscate them independently
                deduplicateStrings(cls);
            } else {
                super.onHeapRecord(tag, reader);
            }
        }
    }

    public Map<ID, HprofString> getStrings() {
        return strings;
    }

    public Map<ID, ClassDefinition> getClasses() {
        return classes;
    }


    public Map<Integer, ClassDefinition> getClassesWithSerialNumbers() {
        return classesWithSerialNumbers;
    }

    public Map<ID,StackFrame> getStackFrames()
    {
        return stackFrames;
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, @Nonnull HprofReader reader) throws IOException {
        InputStream in = reader.getInputStream();
        if (tag == Tag.HEAP_DUMP || tag == Tag.HEAP_DUMP_SEGMENT) {
            byte[] record = read(in, length);
            readHeapDump(record);
        } else if (tag == Tag.STRING) {
            HprofString string = reader.readStringRecord(length, timestamp);
            lastStringId = maxID(lastStringId, string.getId()); // Keep track of the highest string id encountered so that we can add new ids later without having a collision
            strings.put(string.getId(), string);
        } else if (tag == Tag.LOAD_CLASS) { // ClassDefinitions are first created from a LOAD_CLASS record and then populate from a DUMP_CLASS heap record
            ClassDefinition classDef = reader.readLoadClassRecord();
            classes.put(classDef.getObjectId(), classDef);
            classesWithSerialNumbers.put(classDef.getSerialNumber(),classDef);
        } else if (tag == Tag.STACK_FRAME)
        {
//            byte[] record = read(in, length);
            StackFrame stackFrame = reader.readStackFrame();
            deduplicateStackFrameStrings(stackFrame);
            stackFrames.put(stackFrame.getStackFrameId(),stackFrame);


        }else {
            super.onRecord(tag, timestamp, length, reader); // Discard
        }
    }


    private void readHeapDump(byte[] data) throws IOException {
        HeapDumpReader reader = new HeapDumpReader(new ByteArrayInputStream(data), data.length, classDumpProcessor);
        while (reader.hasNext()) {
            reader.next();
        }
    }

    private void deduplicateStrings(ClassDefinition classDef) {
        for (StaticField field : classDef.getStaticFields()) {
            deduplicateFieldName(field);
        }
        for (InstanceField field : classDef.getInstanceFields()) {
            deduplicateFieldName(field);
        }
    }



    private void deduplicateStackFrameStrings(StackFrame stackFrame) {
        deduplicateMethodNameString(stackFrame);
        deduplicateMethodSignatureString(stackFrame);
    }

    private void deduplicateMethodNameString(StackFrame stackFrame) {
        if (referencedStringIds.contains(stackFrame.getMethodNameStringId())) {
            // Create an alias for this string
            HprofString string = strings.get(stackFrame.getMethodNameStringId());
            ID newId = createNewStringId();
            if (strings.containsKey(newId)) {
                throw new IllegalStateException("Failed to generate string id!");
            }

            stackFrame.setMethodNameStringId(newId);
            strings.put(newId, new HprofString(newId, string.getValue(), string.getTimestamp()));
            referencedStringIds.add(newId); // Just in case
        } else {
            referencedStringIds.add(stackFrame.getMethodNameStringId());
        }
    }


    private void deduplicateMethodSignatureString(StackFrame stackFrame) {
        if (referencedStringIds.contains(stackFrame.getMethodSignatureStringId())) {
            // Create an alias for this string
            HprofString string = strings.get(stackFrame.getMethodSignatureStringId());
            ID newId = createNewStringId();
            if (strings.containsKey(newId)) {
                throw new IllegalStateException("Failed to generate string id!");
            }

            stackFrame.setMethodSignatureStringId(newId);
            strings.put(newId, new HprofString(newId, string.getValue(), string.getTimestamp()));
            referencedStringIds.add(newId); // Just in case
        } else {
            referencedStringIds.add(stackFrame.getMethodSignatureStringId());
        }
    }


    private void deduplicateFieldName(NamedField field) {
        if (referencedStringIds.contains(field.getFieldNameId())) {
            // Create an alias for this string
            HprofString string = strings.get(field.getFieldNameId());
            ID newId = createNewStringId();
            if (strings.containsKey(newId)) {
                throw new IllegalStateException("Failed to generate string id!");
            }

            field.setFieldNameId(newId);
            strings.put(newId, new HprofString(newId, string.getValue(), string.getTimestamp()));
            referencedStringIds.add(newId); // Just in case
        } else {
            referencedStringIds.add(field.getFieldNameId());
        }
    }

    private ID createNewStringId() {

        long stringIdLong = lastStringId.toLong();
        stringIdLong++;
        System.out.println("ID_CURR=" + lastStringId);
        lastStringId = new ID(stringIdLong);
        System.out.println("ID_Next="+lastStringId);
        return lastStringId;
    }


    private ID maxID(ID first, ID second) {
        long firstNum = first.toLong()  ;
        long secondNum = second.toLong();

        if (firstNum > secondNum)
            return first;
        else return second;

    }
}
