package com.badoo.hprof.unobfuscator;

import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.Tag;
import com.badoo.hprof.library.heap.HeapDumpReader;
import com.badoo.hprof.library.heap.HeapTag;
import com.badoo.hprof.library.heap.processor.HeapDumpDiscardProcessor;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.library.model.NamedField;
import com.badoo.hprof.library.model.StaticField;
import com.badoo.hprof.library.processor.DiscardProcessor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.badoo.hprof.library.StreamUtil.read;
import static com.badoo.hprof.library.StreamUtil.readInt;
import static com.badoo.hprof.library.StreamUtil.readString;

/**
 * Created by Erik Andre on 13/08/2014.
 */
public class DataCollectionProcessor extends DiscardProcessor {

    private ClassDumpProcessor classDumpProcessor = new ClassDumpProcessor();
    private Map<Integer, HprofString> strings = new HashMap<Integer, HprofString>();
    private Map<Integer, ClassDefinition> classes = new HashMap<Integer, ClassDefinition>();
    private int lastStringId = 0;
    private Set<Integer> referencedStringIds = new HashSet<Integer>();

    public Map<Integer, HprofString> getStrings() {
        return strings;
    }

    public Map<Integer, ClassDefinition> getClasses() {
        return classes;
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, HprofReader reader) throws IOException {
        InputStream in = reader.getInputStream();
        if (tag == Tag.HEAP_DUMP || tag == Tag.HEAP_DUMP_SEGMENT) {
            byte[] record = read(in, length);
            readHeapDump(record);
        }
        else if (tag == Tag.STRING) {
            HprofString string = reader.readStringRecord(length, timestamp);
            lastStringId = Math.max(lastStringId, string.getId()); // Keep track of the highest string id encountered so that we can add new ids later without having a collision
            strings.put(string.getId(), string);
        }
        else if (tag == Tag.LOAD_CLASS) { // ClassDefinitions are first created from a LOAD_CLASS record and then populate from a DUMP_CLASS heap record
            ClassDefinition classDef = reader.readLoadClassRecord();
            classes.put(classDef.getObjectId(), classDef);
        }
        else {
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

    private void deduplicateFieldName(NamedField field) {
        if (referencedStringIds.contains(field.getFieldNameId())) {
            // Create an alias for this string
            HprofString string = strings.get(field.getFieldNameId());
            int newId = createNewStringId();
            if (strings.containsKey(newId)) {
                throw new IllegalStateException("Failed to generate string id!");
            }
            field.setFieldNameId(newId);
            strings.put(newId, new HprofString(newId, string.getValue(), string.getTimestamp()));
            referencedStringIds.add(newId); // Just in case
        }
        else {
            referencedStringIds.add(field.getFieldNameId());
        }
    }

    private int createNewStringId() {
        lastStringId++;
        return lastStringId;
    }

    class ClassDumpProcessor extends HeapDumpDiscardProcessor {

        @Override
        public void onHeapRecord(int tag, HeapDumpReader reader) throws IOException {
            if (tag == HeapTag.CLASS_DUMP) {
                int objectId = readInt(reader.getInputStream());
                ClassDefinition classDef = classes.get(objectId);
                if (classDef == null) {
                    throw new IllegalStateException("Class with id " + objectId + " no loaded before reading class dump!");
                }
                reader.readClassDumpRecord(classDef);
                // Since the names of obfuscated fields are shared between classes we need to deduplicate the references, otherwise we cannot deobfuscate them independently
                deduplicateStrings(classDef);
            }
            else {
                super.onHeapRecord(tag, reader);
            }
        }
    }


}
