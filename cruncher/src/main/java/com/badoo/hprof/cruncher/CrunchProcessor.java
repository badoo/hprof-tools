package com.badoo.hprof.cruncher;

import com.badoo.hprof.cruncher.bmd.BmdTag;
import com.badoo.hprof.cruncher.bmd.DataWriter;
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
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.library.model.StaticField;
import com.badoo.hprof.library.processor.DiscardProcessor;
import com.badoo.hprof.library.util.StreamUtil;
import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Processor for reading a HPROF file and outputting a BMD file.
 *
 * Created by Erik Andre on 22/10/14.
 */
public class CrunchProcessor extends DiscardProcessor {

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
            writeRawBytes(data);
        }

        public void writeClassDefinition(ClassDefinition classDef) throws IOException {
            writeInt32(BmdTag.CLASS_DEFINITION);
            writeInt32(mapObjectId(classDef.getObjectId()));
            writeInt32(mapObjectId(classDef.getSuperClassObjectId()));
            // Write constants and static fields (not filtered)
            int constantFieldCount = classDef.getConstantFields().size();
            writeInt32(constantFieldCount);
            for (int i = 0; i < constantFieldCount; i++) {
                ConstantField field = classDef.getConstantFields().get(i);
                writeInt32(field.getPoolIndex());
                writeInt32(field.getType().type);
                writeFieldValue(field.getType(), field.getValue());
            }
            int staticFieldCount = classDef.getStaticFields().size();
            writeInt32(staticFieldCount);
            for (int i = 0; i < staticFieldCount; i++) {
                StaticField field = classDef.getStaticFields().get(i);
                writeInt32(stringIds.get(field.getFieldNameId()));
                writeInt32(field.getType().type);
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
                writeInt32(field.getType().type);
            }
            writeInt32(0); // End marker for instance fields
            writeInt32(skippedFieldSize);
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
    }

    private class ClassDumpProcessor extends HeapDumpDiscardProcessor {

        @Override
        public void onHeapRecord(int tag, HeapDumpReader reader) throws IOException {
            switch (tag) {
                case HeapTag.CLASS_DUMP:
                    ClassDefinition def = reader.readClassDumpRecord(classes);
                    writer.writeClassDefinition(def);
                    break;
                default:
                    super.onHeapRecord(tag, reader);
            }
        }

    }

    private final CrunchBdmWriter writer;
    private int nextStringId = 1; // Skipping 0 since this is used as a marker in some cases
    private Map<Integer, Integer> stringIds = new HashMap<Integer, Integer>(); // Maps original to updated string ids
    private int nextObjectId = 1;
    private Map<Integer, Integer> objectIds = new HashMap<Integer, Integer>(); // Maps original to updated object/class ids
    private Map<Integer, ClassDefinition> classes = new HashMap<Integer, ClassDefinition>();

    public CrunchProcessor(OutputStream out) {
        this.writer = new CrunchBdmWriter(out);
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, HprofReader reader) throws IOException {
        switch (tag) {
            case Tag.STRING:
                HprofString string = reader.readStringRecord(length, timestamp);
                // We replace the original string id with one starting from 0 as these are more efficient to store
                stringIds.put(string.getId(), nextStringId); // Save the original id so we can update references later
                string.setId(nextStringId);
                nextStringId++;
                writer.writeString(string, true);
                break;
            case Tag.LOAD_CLASS:
                ClassDefinition classDef = reader.readLoadClassRecord();
                classes.put(classDef.getObjectId(), classDef);
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
                byte[] data = StreamUtil.read(reader.getInputStream(), length);
                writer.writeLegacyRecord(tag, data);
                break;
        }
    }

    @Override
    public void onHeader(String text, int idSize, int timeHigh, int timeLow) throws IOException {
        writer.writeHeader(1, text.getBytes());
    }

    private int mapObjectId(int id) {
        if (!objectIds.containsKey(id)) {
            objectIds.put(id, nextObjectId);
            nextObjectId++;
        }
        return objectIds.get(id);
    }
}
