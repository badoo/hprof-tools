package com.badoo.hprof.cruncher;

import com.badoo.bmd.BmdTag;
import com.badoo.bmd.DataWriter;
import com.badoo.bmd.model.BmdBasicType;
import com.badoo.hprof.cruncher.util.CodingUtil;
import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.Tag;
import com.badoo.hprof.library.heap.HeapDumpReader;
import com.badoo.hprof.library.heap.HeapTag;
import com.badoo.hprof.library.heap.processor.HeapDumpDiscardProcessor;
import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.ConstantField;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.library.model.StaticField;
import com.badoo.hprof.library.processor.DiscardProcessor;
import com.sun.istack.internal.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.badoo.hprof.library.util.StreamUtil.read;
import static com.badoo.hprof.library.util.StreamUtil.readInt;
import static com.badoo.hprof.library.util.StreamUtil.writeInt;

/**
 * Processor for reading a HPROF file and outputting a BMD file.
 * <p/>
 * Created by Erik Andre on 22/10/14.
 */
public class CrunchProcessor extends DiscardProcessor {

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ForLoopReplaceableByForEach"})
    private class CrunchBdmWriter extends DataWriter {

        protected CrunchBdmWriter(OutputStream out) {
            super(out);
        }

        public void writeHeader(int version, @Nullable byte[] metadata) throws IOException {
            writeInt32(version);
            writeByteArrayWithLength(metadata != null ? metadata : new byte[]{});
        }

        public void writeString(HprofString string, boolean hashed) throws IOException {
            writeInt32(hashed ? BmdTag.HASHED_STRING : BmdTag.STRING);
            writeInt32(string.getId());
            if (hashed) {
                writeInt32(string.getValue().hashCode());
            }
            else {
                writeByteArrayWithLength(string.getValue().getBytes());
            }
        }

        public void writeLegacyRecord(int tag, byte[] data) throws IOException {
            writeInt32(BmdTag.LEGACY_HPROF_RECORD);
            writeInt32(tag);
            writeInt32(data.length);
            writeRawBytes(data);
        }

        public void writeClassDefinition(ClassDefinition classDef) throws IOException {
            writeInt32(BmdTag.CLASS_DEFINITION);
            writeInt32(mapObjectId(classDef.getObjectId()));
            writeInt32(mapObjectId(classDef.getSuperClassObjectId()));
            writeInt32(stringIds.get(classDef.getNameStringId()));
            // Write constants and static fields (not filtered)
            int constantFieldCount = classDef.getConstantFields().size();
            writeInt32(constantFieldCount);
            for (int i = 0; i < constantFieldCount; i++) {
                ConstantField field = classDef.getConstantFields().get(i);
                writeInt32(field.getPoolIndex());
                writeInt32(convertType(field.getType()).id);
                writeFieldValue(field.getType(), field.getValue());
            }
            int staticFieldCount = classDef.getStaticFields().size();
            writeInt32(staticFieldCount);
            for (int i = 0; i < staticFieldCount; i++) {
                StaticField field = classDef.getStaticFields().get(i);
                writeInt32(stringIds.get(field.getFieldNameId()));
                writeInt32(convertType(field.getType()).id);
                writeFieldValue(field.getType(), field.getValue());
            }
            // Filter instance fields before writing them
            int skippedFieldSize = 0;
            int instanceFieldCount = classDef.getInstanceFields().size();
            for (int i = 0; i < instanceFieldCount; i++) {
                InstanceField field = classDef.getInstanceFields().get(i);
                if (field.getType() != BasicType.OBJECT) {
                    skippedFieldSize += field.getType().size;
                    continue;
                }
                writeInt32(stringIds.get(field.getFieldNameId()));
                writeInt32(convertType(field.getType()).id);
            }
            writeInt32(0); // End marker for instance fields
            writeInt32(skippedFieldSize);
        }

        public void writeInstanceDump(Instance instance) throws IOException {
            writeInt32(BmdTag.INSTANCE_DUMP);
            writeInt32(mapObjectId(instance.getObjectId()));
            writeInt32(mapObjectId(instance.getClassObjectId()));
            ClassDefinition currentClass = classesByOriginalId.get(instance.getClassObjectId());
            ByteArrayInputStream in = new ByteArrayInputStream(instance.getInstanceFieldData());
            while (currentClass != null) {
                int fieldCount = currentClass.getInstanceFields().size();
                for (int i = 0; i < fieldCount; i++) {
                    InstanceField field = currentClass.getInstanceFields().get(i);
                    BasicType type = field.getType();
                    if (type == BasicType.OBJECT) {
                        int id = readInt(in);
                        writeInt32(mapObjectId(id));
                    } else { // Other fields are ignored
                        in.skip(type.size);
                    }
                }
                currentClass = classesByOriginalId.get(currentClass.getSuperClassObjectId());
            }
            if (in.available() != 0) {
                throw new IllegalStateException("Did not read the expected number of bytes. Available: " + in.available());
            }
        }

        public void writePrimitiveArray(int originalObjectId, BasicType type, int count) throws IOException {
            writeInt32(BmdTag.PRIMITIVE_ARRAY_PLACEHOLDER);
            writeInt32(mapObjectId(originalObjectId));
            writeInt32(convertType(type).id);
            writeInt32(count);
        }

        public void writeObjectArray(int originalObjectId, int originalClassId, int[] elements) throws IOException {
            writeInt32(BmdTag.OBJECT_ARRAY);
            writeInt32(mapObjectId(originalObjectId));
            writeInt32(mapObjectId(originalClassId));
            writeInt32(elements.length);
            for (int i = 0; i < elements.length; i++) {
                writeInt32(mapObjectId(elements[i]));
            }
        }

        public void writeRootObjects(List<Integer> roots) throws IOException {
            writeInt32(BmdTag.ROOT_OBJECTS);
            writeInt32(roots.size());
            for (int i = 0; i < roots.size(); i++) {
                writeInt32(mapObjectId(roots.get(i)));
            }
        }

        private void writeFieldValue(BasicType type, byte[] data) throws IOException {
            switch (type) {
                case OBJECT:
                    writeInt32(mapObjectId(CodingUtil.readInt(data)));
                    break;
                case SHORT:
                    writeInt32(CodingUtil.readShort(data));
                    break;
                case INT:
                    writeInt32(CodingUtil.readInt(data));
                    break;
                case LONG:
                    writeInt64(CodingUtil.readLong(data));
                    break;
                case FLOAT:
                    writeFloat(Float.intBitsToFloat(CodingUtil.readInt(data)));
                    break;
                case DOUBLE:
                    writeDouble(Double.longBitsToDouble(CodingUtil.readLong(data)));
                    break;
                case BOOLEAN:
                    writeRawBytes(data);
                    break;
                case BYTE:
                    writeRawBytes(data);
                    break;
                case CHAR:
                    writeRawBytes(data);
                    break;
            }
        }

        private BmdBasicType convertType(BasicType type) {
            switch (type) {
                case OBJECT:
                    return BmdBasicType.OBJECT;
                case BOOLEAN:
                    return BmdBasicType.BOOLEAN;
                case BYTE:
                    return BmdBasicType.BYTE;
                case CHAR:
                    return BmdBasicType.CHAR;
                case SHORT:
                    return BmdBasicType.SHORT;
                case INT:
                    return BmdBasicType.INT;
                case LONG:
                    return BmdBasicType.LONG;
                case FLOAT:
                    return BmdBasicType.FLOAT;
                case DOUBLE:
                    return BmdBasicType.DOUBLE;
                default:
                    throw new IllegalArgumentException("Invalid type:" + type);
            }
        }
    }

    private class ClassDumpProcessor extends HeapDumpDiscardProcessor {

        @Override
        public void onHeapRecord(int tag, HeapDumpReader reader) throws IOException {
            switch (tag) {
                case HeapTag.CLASS_DUMP:
                    ClassDefinition def = reader.readClassDumpRecord(classesByOriginalId);
                    writer.writeClassDefinition(def);
                    break;
                default:
                    super.onHeapRecord(tag, reader);
            }
        }

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private class ObjectDumpProcessor extends HeapDumpDiscardProcessor {

        @Override
        public void onHeapRecord(int tag, HeapDumpReader reader) throws IOException {
            InputStream in = reader.getInputStream();
            switch (tag) {
                case HeapTag.INSTANCE_DUMP:
                    Instance instance = reader.readInstanceDump();
                    writer.writeInstanceDump(instance);
                    break;
                case HeapTag.OBJECT_ARRAY_DUMP:
                    readObjectArray(in);
                    break;
                case HeapTag.PRIMITIVE_ARRAY_DUMP:
                    readPrimitiveArray(in);
                    break;
                // Roots
                case HeapTag.ROOT_UNKNOWN:
                    roots.add(readInt(in));
                    break;
                case HeapTag.ROOT_JNI_GLOBAL:
                    roots.add(readInt(in));
                    in.skip(4); // JNI global ref
                    break;
                case HeapTag.ROOT_JNI_LOCAL:
                    roots.add(readInt(in));
                    in.skip(8); // Thread serial + frame number
                    break;
                case HeapTag.ROOT_JAVA_FRAME:
                    roots.add(readInt(in));
                    in.skip(8); // Thread serial + frame number
                    break;
                case HeapTag.ROOT_NATIVE_STACK:
                    roots.add(readInt(in));
                    in.skip(4); // Thread serial
                    break;
                case HeapTag.ROOT_STICKY_CLASS:
                    roots.add(readInt(in));
                    break;
                case HeapTag.ROOT_THREAD_BLOCK:
                    roots.add(readInt(in));
                    in.skip(4); // Thread serial
                    break;
                case HeapTag.ROOT_MONITOR_USED:
                    roots.add(readInt(in));
                    break;
                case HeapTag.ROOT_THREAD_OBJECT:
                    roots.add(readInt(in));
                    in.skip(8); // Thread serial + stack serial
                    break;
                case HeapTag.HPROF_ROOT_INTERNED_STRING:
                    roots.add(readInt(in));
                    break;
                case HeapTag.HPROF_ROOT_FINALIZING:
                    roots.add(readInt(in));
                    break;
                case HeapTag.HPROF_ROOT_DEBUGGER:
                    roots.add(readInt(in));
                    break;
                case HeapTag.HPROF_ROOT_REFERENCE_CLEANUP:
                    roots.add(readInt(in));
                    break;
                case HeapTag.HPROF_ROOT_VM_INTERNAL:
                    roots.add(readInt(in));
                    break;
                case HeapTag.HPROF_ROOT_JNI_MONITOR:
                    roots.add(readInt(in));
                    in.skip(8); // Data
                    break;
                default:
                    super.onHeapRecord(tag, reader);
            }
        }

        private void readObjectArray(InputStream in) throws IOException {
            int originalObjectId = readInt(in);
            in.skip(4); // Stack trace serial
            int count = readInt(in);
            int originalElementClassId = readInt(in);
            int[] elements = new int[count];
            for (int i = 0; i < count; i++) {
                elements[i] = readInt(in);
            }
            writer.writeObjectArray(originalObjectId, originalElementClassId, elements);
        }

        private void readPrimitiveArray(InputStream in) throws IOException {
            int originalObjectId = readInt(in);
            in.skip(4); // Stack trace serial
            int count = readInt(in);
            BasicType type = BasicType.fromType(in.read());
            in.skip(count * type.size);
            writer.writePrimitiveArray(originalObjectId, type, count);
        }

    }

    private final CrunchBdmWriter writer;
    private int nextStringId = 1; // Skipping 0 since this is used as a marker in some cases
    private Map<Integer, Integer> stringIds = new HashMap<Integer, Integer>(); // Maps original to updated string ids
    private int nextObjectId = 1;
    private Set<Integer> mappedIds = new HashSet<Integer>();
    private Map<Integer, Integer> objectIds = new HashMap<Integer, Integer>(); // Maps original to updated object/class ids
    private Map<Integer, ClassDefinition> classesByOriginalId = new HashMap<Integer, ClassDefinition>(); // Maps original class id to the class definition
    private boolean readObjects;
    private List<Integer> roots = new ArrayList<Integer>();

    public CrunchProcessor(OutputStream out) {
        this.writer = new CrunchBdmWriter(out);
    }

    public void allClassesRead() {
        readObjects = true;
    }

    public void finish() throws IOException {
        // Write roots
        writer.writeRootObjects(roots);
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, HprofReader reader) throws IOException {
        if (!readObjects) { // 1st pass: read class definitions and strings
            switch (tag) {
                case Tag.STRING:
                    HprofString string = reader.readStringRecord(length, timestamp);
                    // We replace the original string id with one starting from 0 as these are more efficient to store
                    stringIds.put(string.getId(), nextStringId); // Save the original id so we can update references later
                    string.setId(nextStringId);
                    nextStringId++;
                    boolean hashed = !(string.getValue().startsWith("java.lang")); // Keep real names for some system classes (needed until decruncher can unscramble hashed strings)
                    writer.writeString(string, hashed);
                    break;
                case Tag.LOAD_CLASS:
                    ClassDefinition classDef = reader.readLoadClassRecord();
                    classesByOriginalId.put(classDef.getObjectId(), classDef);
                    break;
                case Tag.HEAP_DUMP:
                case Tag.HEAP_DUMP_SEGMENT:
                    ClassDumpProcessor dumpProcessor = new ClassDumpProcessor();
                    HeapDumpReader dumpReader = new HeapDumpReader(reader.getInputStream(), length, dumpProcessor);
                    while (dumpReader.hasNext()) {
                        dumpReader.next();
                    }
                    break;
                case Tag.UNLOAD_CLASS:
                case Tag.HEAP_DUMP_END:
                    super.onRecord(tag, timestamp, length, reader); // These records can be discarded
                    break;
                default:
                    byte[] data = read(reader.getInputStream(), length);
                    writer.writeLegacyRecord(tag, data);
                    break;
            }
        }
        else { // 2nd pass: read object dumps
            switch (tag) {
                case Tag.HEAP_DUMP:
                case Tag.HEAP_DUMP_SEGMENT:
                    ObjectDumpProcessor dumpProcessor = new ObjectDumpProcessor();
                    HeapDumpReader dumpReader = new HeapDumpReader(reader.getInputStream(), length, dumpProcessor);
                    while (dumpReader.hasNext()) {
                        dumpReader.next();
                    }
                    break;
                default:
                    super.onRecord(tag, timestamp, length, reader); // Skip record
            }
        }
    }

    @Override
    public void onHeader(String text, int idSize, int timeHigh, int timeLow) throws IOException {
        writer.writeHeader(1, text.getBytes());
    }

    private int mapObjectId(int id) {
        if (id == 0) {
            return 0; // Zero is a special case used when there is no value (null), do not map it to a new id
        }
        if (!objectIds.containsKey(id)) {
            mappedIds.add(nextObjectId);
            objectIds.put(id, nextObjectId);
            nextObjectId++;
        }
        if (mappedIds.contains(id)) {
            throw new IllegalArgumentException("Trying to map an already mapped id! " + id);
        }
        return objectIds.get(id);
    }
}
