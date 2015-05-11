package com.badoo.hprof.cruncher;

import com.badoo.bmd.BmdTag;
import com.badoo.bmd.DataWriter;
import com.badoo.bmd.model.BmdBasicType;
import com.badoo.hprof.cruncher.util.CodingUtil;
import com.badoo.hprof.cruncher.util.Stats;
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
import com.badoo.hprof.library.model.ObjectArray;
import com.badoo.hprof.library.model.PrimitiveArray;
import com.badoo.hprof.library.model.StaticField;
import com.badoo.hprof.library.processor.DiscardProcessor;
import com.google.common.io.CountingOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import static com.badoo.hprof.library.util.StreamUtil.read;
import static com.badoo.hprof.library.util.StreamUtil.readInt;
import static com.badoo.hprof.library.util.StreamUtil.skip;

/**
 * Processor for reading a HPROF file and outputting a BMD file. Operates in two stages:
 * <p/>
 * 1. Read all class definitions and write them to the BMD file.
 * 2. Read all instance dumps and write them to the BMD file.
 * <p/>
 * The reason why it's being done in two steps is that in HPROF files the class definition is not guaranteed to come
 * before the instance dump. In order to process it in one pass you must keep all the class definitions and some instance dumps in memory
 * until all class dependencies can be resolved.
 * <p/>
 * Created by Erik Andre on 22/10/14.
 */
public class CrunchProcessor extends DiscardProcessor {

    private static final int FIRST_ID = 1; // Skipping 0 since this is used as a (null) marker in some cases

    private final CrunchBdmWriter writer;
    private final boolean collectStats;
    private int nextStringId = FIRST_ID;
    private Map<Integer, Integer> stringIds = new HashMap<Integer, Integer>(); // Maps original to updated string ids
    private int nextObjectId = FIRST_ID;
    private Map<Integer, Integer> objectIds = new HashMap<Integer, Integer>(); // Maps original to updated object/class ids
    private Map<Integer, ClassDefinition> classesByOriginalId = new HashMap<Integer, ClassDefinition>(); // Maps original class id to the class definition
    private boolean readObjects;
    private List<Integer> rootObjectIds = new ArrayList<Integer>();

    public CrunchProcessor(OutputStream out, boolean collectStats) {
        this.writer = new CrunchBdmWriter(out);
        this.collectStats = collectStats;
    }

    /**
     * Must be called after the first pass (where class data is processed) is finished, before the second pass is started.
     */
    public void startSecondPass() {
        readObjects = true;
    }

    /**
     * Call after the second has has finished to write any remaining BMD data to the output stream and finish the conversion process.
     */
    public void finishAndWriteOutput() throws IOException {
        // Write roots
        writer.writeRootObjects(rootObjectIds);
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, @Nonnull HprofReader reader) throws IOException {
        if (!readObjects) { // 1st pass: read class definitions and strings
            switch (tag) {
                case Tag.STRING:
                    readStringRecord(timestamp, length, reader);
                    break;
                case Tag.LOAD_CLASS:
                    if (collectStats) {
                        Stats.increment(Stats.StatType.CLASS_HPROF, length + 9);
                    }
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

    private void readStringRecord(int timestamp, int length, HprofReader reader) throws IOException {
        if (collectStats) {
            Stats.increment(Stats.StatType.STRING_HPROF, length + 9);
        }
        HprofString string = reader.readStringRecord(length, timestamp);
        // We replace the original string id with one starting from 1 as these are more efficient to store
        string.setId(mapStringId(string.getId()));
        boolean hashed = !keepString(string.getValue());
        final long start = writer.getCurrentPosition();
        writer.writeString(string, hashed);
        if (collectStats) {
            Stats.increment(Stats.StatType.STRING_BMD, writer.getCurrentPosition() - start);
        }
    }

    private boolean keepString(String string) {
        // Keep the names of some core system classes (to avoid issues in MAT)
        return string.startsWith("java.lang") || "V".equals(string) || "boolean".equals(string) || "byte".equals(string)
            || "short".equals(string) || "char".equals(string) || "int".equals(string) || "long".equals(string)
            || "float".equals(string) || "double".equals(string);
    }

    @Override
    public void onHeader(@Nonnull String text, int idSize, int timeHigh, int timeLow) throws IOException {
        // The text of the HPROF header is written to the BMD header but the timestamp is discarded
        writer.writeHeader(1, text.getBytes());
    }

    /**
     * Map an original HPROF object id to an updated object id (that is more efficient to store in BMD format)
     *
     * @param originalId the original object id
     * @return an updated object id
     */
    private int mapObjectId(int originalId) {
        if (originalId == 0) {
            return 0; // Zero is a special case used when there is no value (null), do not map it to a new id
        }
        if (!objectIds.containsKey(originalId)) {
            objectIds.put(originalId, nextObjectId);
            nextObjectId++;
        }
        return objectIds.get(originalId);
    }

    /**
     * Map an original HPROF string id to an updated string id (that is more efficient to store in BMD format)
     *
     * @param originalId the original string id
     * @return an updated string id
     */
    private int mapStringId(int originalId) {
        if (originalId == 0) {
            return 0; // Zero is a special case used when there is no value (null), do not map it to a new id
        }
        if (!stringIds.containsKey(originalId)) {
            stringIds.put(originalId, nextStringId);
            nextStringId++;
        }
        return stringIds.get(originalId);
    }

    @SuppressWarnings({"ForLoopReplaceableByForEach"})
    private class CrunchBdmWriter extends DataWriter {

        private CountingOutputStream cOut;

        protected CrunchBdmWriter(OutputStream out) {
            super(new CountingOutputStream(out));
            cOut = (CountingOutputStream) getOutputStream();
        }

        public long getCurrentPosition() {
            return cOut.getCount();
        }

        public void writeHeader(int version, byte[] metadata) throws IOException {
            writeInt32(version);
            writeByteArrayWithLength(metadata != null ? metadata : new byte[]{});
        }

        public void writeString(HprofString string, boolean hashed) throws IOException {
            writeTag(hashed ? BmdTag.HASHED_STRING : BmdTag.STRING);
            writeInt32(string.getId());
            byte[] stringData = string.getValue().getBytes();
            if (hashed) {
                writeRawVarint32(stringData.length);
                writeInt32(string.getValue().hashCode());
            }
            else {
                writeByteArrayWithLength(stringData);
            }
        }

        public void writeLegacyRecord(int tag, byte[] data) throws IOException {
            writeInt32(BmdTag.LEGACY_HPROF_RECORD.value);
            writeInt32(tag);
            writeInt32(data.length);
            writeRawBytes(data);
        }

        public void writeClassDefinition(ClassDefinition classDef) throws IOException {
            final long start = getCurrentPosition();
            writeTag(BmdTag.CLASS_DEFINITION);
            writeInt32(mapObjectId(classDef.getObjectId()));
            writeInt32(mapObjectId(classDef.getSuperClassObjectId()));
            writeInt32(mapStringId(classDef.getNameStringId()));
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
                writeInt32(mapStringId(field.getFieldNameId()));
                writeInt32(convertType(field.getType()).id);
                writeFieldValue(field.getType(), field.getValue());
            }
            // Filter instance fields before writing them
            int skippedFieldSize = 0;
            List<InstanceField> keptFields = new ArrayList<InstanceField>();
            int instanceFieldCount = classDef.getInstanceFields().size();
            for (int i = 0; i < instanceFieldCount; i++) {
                InstanceField field = classDef.getInstanceFields().get(i);
                if (field.getType() != BasicType.OBJECT) {
                    skippedFieldSize += field.getType().size;
                }
                else {
                    keptFields.add(field);
                }
            }
            int keptFieldCount = keptFields.size();
            writeInt32(keptFieldCount);
            for (int i = 0; i < keptFieldCount; i++) {
                InstanceField field = keptFields.get(i);
                writeInt32(mapStringId(field.getFieldNameId()));
                writeInt32(convertType(field.getType()).id);
            }
            writeInt32(skippedFieldSize);
            if (collectStats) {
                Stats.increment(Stats.StatType.CLASS_BMD, writer.getCurrentPosition() - start);
            }
        }

        public void writeInstanceDump(Instance instance) throws IOException {
            final long start = getCurrentPosition();
            writeTag(BmdTag.INSTANCE_DUMP);
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
                    }
                    else { // Other fields are ignored
                        skip(in, type.size);
                    }
                }
                currentClass = classesByOriginalId.get(currentClass.getSuperClassObjectId());
            }
            if (in.available() != 0) {
                throw new IllegalStateException("Did not read the expected number of bytes. Available: " + in.available());
            }
            if (collectStats) {
                Stats.increment(Stats.StatType.INSTANCE_BMD, writer.getCurrentPosition() - start);
            }
        }

        public void writePrimitiveArray(PrimitiveArray array) throws IOException {
            final long start = getCurrentPosition();
            writeTag(BmdTag.PRIMITIVE_ARRAY_PLACEHOLDER);
            writeInt32(mapObjectId(array.getObjectId()));
            writeInt32(convertType(array.getType()).id);
            writeInt32(array.getCount());
            if (collectStats) {
                Stats.increment(Stats.StatType.ARRAY_BMD, writer.getCurrentPosition() - start);
            }
        }

        public void writeObjectArray(ObjectArray array) throws IOException {
            final long start = getCurrentPosition();
            writeTag(BmdTag.OBJECT_ARRAY);
            writeInt32(mapObjectId(array.getObjectId()));
            writeInt32(mapObjectId(array.getElementClassId()));
            writeInt32(array.getCount());
            for (int i = 0; i < array.getCount(); i++) {
                writeInt32(mapObjectId(array.getElements()[i]));
            }
            if (collectStats) {
                Stats.increment(Stats.StatType.ARRAY_BMD, writer.getCurrentPosition() - start);
            }
        }

        public void writeRootObjects(List<Integer> roots) throws IOException {
            writeTag(BmdTag.ROOT_OBJECTS);
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

        private void writeTag(BmdTag tag) throws IOException {
            writeInt32(tag.value);
        }
    }

    // 1st pass dump processor
    private class ClassDumpProcessor extends HeapDumpDiscardProcessor {

        @Override
        public void onHeapRecord(int tag, @Nonnull HeapDumpReader reader) throws IOException {
            switch (tag) {
                case HeapTag.CLASS_DUMP:
                    final long start = reader.getCurrentPosition();
                    ClassDefinition def = reader.readClassDumpRecord(classesByOriginalId);
                    if (collectStats) {
                        final long length = reader.getCurrentPosition() - start;
                        Stats.increment(Stats.StatType.CLASS_HPROF, length);
                    }
                    writer.writeClassDefinition(def);
                    break;
                default:
                    super.onHeapRecord(tag, reader);
            }
        }

    }

    // 1st pass dump processor
    private class ObjectDumpProcessor extends HeapDumpDiscardProcessor {

        @Override
        public void onHeapRecord(int tag, @Nonnull HeapDumpReader reader) throws IOException {
            InputStream in = reader.getInputStream();
            long start = reader.getCurrentPosition();
            switch (tag) {
                case HeapTag.INSTANCE_DUMP: {
                    Instance instance = reader.readInstanceDump();
                    writer.writeInstanceDump(instance);
                    if (collectStats) {
                        final long length = reader.getCurrentPosition() - start;
                        Stats.increment(Stats.StatType.INSTANCE_HPROF, length);
                    }
                    break;
                }
                case HeapTag.OBJECT_ARRAY_DUMP:
                    readObjectArray(reader);
                    if (collectStats) {
                        final long length = reader.getCurrentPosition() - start;
                        Stats.increment(Stats.StatType.ARRAY_HPROF, length);
                    }
                    break;
                case HeapTag.PRIMITIVE_ARRAY_DUMP:
                    readPrimitiveArray(reader);
                    if (collectStats) {
                        final long length = reader.getCurrentPosition() - start;
                        Stats.increment(Stats.StatType.ARRAY_HPROF, length);
                    }
                    break;
                // Roots
                case HeapTag.ROOT_UNKNOWN:
                    rootObjectIds.add(readInt(in));
                    break;
                case HeapTag.ROOT_JNI_GLOBAL:
                    rootObjectIds.add(readInt(in));
                    skip(in, 4); // JNI global ref
                    break;
                case HeapTag.ROOT_JNI_LOCAL:
                    rootObjectIds.add(readInt(in));
                    skip(in, 8); // Thread serial + frame number
                    break;
                case HeapTag.ROOT_JAVA_FRAME:
                    rootObjectIds.add(readInt(in));
                    skip(in, 8); // Thread serial + frame number
                    break;
                case HeapTag.ROOT_NATIVE_STACK:
                    rootObjectIds.add(readInt(in));
                    skip(in, 4); // Thread serial
                    break;
                case HeapTag.ROOT_STICKY_CLASS:
                    rootObjectIds.add(readInt(in));
                    break;
                case HeapTag.ROOT_THREAD_BLOCK:
                    rootObjectIds.add(readInt(in));
                    skip(in, 4); // Thread serial
                    break;
                case HeapTag.ROOT_MONITOR_USED:
                    rootObjectIds.add(readInt(in));
                    break;
                case HeapTag.ROOT_THREAD_OBJECT:
                    rootObjectIds.add(readInt(in));
                    skip(in, 8); // Thread serial + stack serial
                    break;
                case HeapTag.HPROF_ROOT_INTERNED_STRING:
                    rootObjectIds.add(readInt(in));
                    break;
                case HeapTag.HPROF_ROOT_FINALIZING:
                    rootObjectIds.add(readInt(in));
                    break;
                case HeapTag.HPROF_ROOT_DEBUGGER:
                    rootObjectIds.add(readInt(in));
                    break;
                case HeapTag.HPROF_ROOT_REFERENCE_CLEANUP:
                    rootObjectIds.add(readInt(in));
                    break;
                case HeapTag.HPROF_ROOT_VM_INTERNAL:
                    rootObjectIds.add(readInt(in));
                    break;
                case HeapTag.HPROF_ROOT_JNI_MONITOR:
                    rootObjectIds.add(readInt(in));
                    skip(in, 8); // Data
                    break;
                default:
                    super.onHeapRecord(tag, reader);
            }
        }

        private void readObjectArray(HeapDumpReader reader) throws IOException {
            ObjectArray array = reader.readObjectArray();
            writer.writeObjectArray(array);
        }

        private void readPrimitiveArray(HeapDumpReader reader) throws IOException {
            PrimitiveArray array = reader.readPrimitiveArray();
            writer.writePrimitiveArray(array);
        }

    }
}
