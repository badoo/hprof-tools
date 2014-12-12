package com.badoo.bmd.decruncher;

import com.badoo.bmd.BmdProcessor;
import com.badoo.bmd.BmdReader;
import com.badoo.bmd.BmdTag;
import com.badoo.bmd.model.BmdBasicType;
import com.badoo.bmd.model.BmdClassDefinition;
import com.badoo.bmd.model.BmdConstantField;
import com.badoo.bmd.model.BmdInstanceDump;
import com.badoo.bmd.model.BmdInstanceFieldDefinition;
import com.badoo.bmd.model.BmdLegacyRecord;
import com.badoo.bmd.model.BmdObjectArray;
import com.badoo.bmd.model.BmdPrimitiveArray;
import com.badoo.bmd.model.BmdStaticField;
import com.badoo.bmd.model.BmdString;
import com.badoo.hprof.library.HprofWriter;
import com.badoo.hprof.library.Tag;
import com.badoo.hprof.library.heap.HeapDumpWriter;
import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.ConstantField;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.library.model.StaticField;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Document
 * Created by Erik Andre on 02/11/14.
 */
public class DecrunchProcessor implements BmdProcessor {

    private static final int FILLER_FIELD_NAME = Integer.MAX_VALUE; //TODO Map this to a real string
    private final HprofWriter writer;
    private final Map<Integer, BmdClassDefinition> classes = new HashMap<Integer, BmdClassDefinition>();
    private List<BmdInstanceDump> instances = new ArrayList<BmdInstanceDump>();
    private List<BmdObjectArray> objectArrays = new ArrayList<BmdObjectArray>();
    private List<BmdPrimitiveArray> primitiveArrays = new ArrayList<BmdPrimitiveArray>();
    private List<Integer> rootObjects = new ArrayList<Integer>();

    public DecrunchProcessor(OutputStream out) {
        writer = new HprofWriter(out);
    }

    /**
     * Write all heap records (instance dumps, primitive arrays and object arrays) previously collected in the read pass.
     */
    public void writeHeapRecords() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        HeapDumpWriter heapWriter = new HeapDumpWriter(buffer);
        // Write class definitions before any instance dumps
        for (BmdClassDefinition bmdClassDef : classes.values()) {
            heapWriter.writeClassDumpRecord(convertClassDefinition(bmdClassDef));
        }
        // Instance dumps

        // Object arrays

        // Primitive arrays

        // GC roots
        byte[] record = buffer.toByteArray();
        writer.writeRecordHeader(Tag.HEAP_DUMP, 0, record.length);
        writer.getOutputStream().write(record);
    }

    @Override
    public void onRecord(int tag, BmdReader reader) throws IOException {
        switch (tag) {
            case BmdTag.STRING:
                writeString(reader.readString());
                break;
            case BmdTag.HASHED_STRING:
                writeString(reader.readHashedString());
                break;
            case BmdTag.CLASS_DEFINITION:
                BmdClassDefinition classDef = reader.readClassDefinition();
                classes.put(classDef.getId(), classDef);
                // Write the load class record now and the class dump later
                writeLoadClassRecord(classDef);
                break;
            case BmdTag.INSTANCE_DUMP:
                instances.add(reader.readInstanceDump(classes));
                break;
            case BmdTag.OBJECT_ARRAY:
                objectArrays.add(reader.readObjectArray());
                break;
            case BmdTag.PRIMITIVE_ARRAY_PLACEHOLDER:
                primitiveArrays.add(reader.readPrimitiveArray());
                break;
            case BmdTag.ROOT_OBJECTS:
                int rootCount = reader.readInt32();
                for (int i = 0; i < rootCount; i++) {
                    rootObjects.add(reader.readInt32());
                }
                break;
            case BmdTag.LEGACY_HPROF_RECORD:
                BmdLegacyRecord legacyRecord = reader.readLegacyRecord();
                writeLegacyRecord(legacyRecord);
                break;
        }
    }

    private ClassDefinition convertClassDefinition(BmdClassDefinition bmdClassDef) {
        ClassDefinition classDef = new ClassDefinition();
        classDef.setObjectId(bmdClassDef.getId());
        classDef.setNameStringId(bmdClassDef.getName());
        classDef.setSuperClassObjectId(bmdClassDef.getSuperClassId());
        // Constant fields
        List<ConstantField> constFields = new ArrayList<ConstantField>();
        for (BmdConstantField field : bmdClassDef.getConstantFields()) {
            BasicType type = convertType(field.getType());
            byte[] value = getFieldValue(field);
            constFields.add(new ConstantField((short) field.getIndex(), type, value));
        }
        classDef.setConstantFields(constFields);
        // Static fields
        List<StaticField> staticFields = new ArrayList<StaticField>();
        for (BmdStaticField field : bmdClassDef.getStaticFields()) {
            BasicType type = convertType(field.getType());
            byte[] value = getFieldValue(field);
            staticFields.add(new StaticField(type, value, field.getNameId()));
        }
        classDef.setStaticFields(staticFields);
        // Instance fields
        List<InstanceField> instanceFields = new ArrayList<InstanceField>();
        for (BmdInstanceFieldDefinition field : bmdClassDef.getInstanceFields()) {
            BasicType type = convertType(field.getType());
            instanceFields.add(new InstanceField(type, field.getNameId()));
        }
        // Add extra instance fields replacing removed fields
        int filler = bmdClassDef.getDiscardedFieldSize();
        while (filler > 0) {
            if (filler >= 4) {
                // Add int
                instanceFields.add(new InstanceField(BasicType.INT, FILLER_FIELD_NAME));
                filler -= 4;
            } else {
                // Add byte
                instanceFields.add(new InstanceField(BasicType.BYTE, FILLER_FIELD_NAME));
                filler -= 1;
            }
        }
        classDef.setInstanceFields(instanceFields);
        // Calculate instance size
        int instanceSize = 0;
        for (InstanceField field : instanceFields) {
            instanceSize += field.getType().size;
        }
        classDef.setInstanceSize(instanceSize);
        return classDef;
    }

    private void writeLegacyRecord(BmdLegacyRecord record) throws IOException {
        writer.writeRecordHeader(record.getOriginalTag(), 0, record.getData().length);
        writer.getOutputStream().write(record.getData());
    }

    private void writeLoadClassRecord(BmdClassDefinition classDef) throws IOException {
        ClassDefinition hprofCls = new ClassDefinition();
        hprofCls.setObjectId(classDef.getId());
        hprofCls.setNameStringId(classDef.getName());
        writer.writeLoadClassRecord(hprofCls);
    }

    private void writeString(BmdString string) throws IOException {
        String stringVal = string.getString() != null? string.getString() : "Hash=" + string.getHash();
        HprofString hprofString = new HprofString(string.getId(), stringVal, 0);
        writer.writeStringRecord(hprofString);
    }

    @Override
    public void onHeader(int version, byte[] data) throws IOException {
        System.out.println("Header version:" + version + ", data=" + new String(data));
        writer.writeHprofFileHeader(new String(data), 4, 0, 0);
    }

    private byte[] getFieldValue(BmdConstantField field) {
        return new byte[0]; //TODO Implement
    }

    private byte[] getFieldValue(BmdStaticField field) {
        return new byte[0]; //TODO Implement
    }

    private BasicType convertType(BmdBasicType type) {
        switch (type) {
            case BOOLEAN:
                return BasicType.BOOLEAN;
            case BYTE:
                return BasicType.BYTE;
            case CHAR:
                return BasicType.CHAR;
            case DOUBLE:
                return BasicType.DOUBLE;
            case FLOAT:
                return BasicType.FLOAT;
            case INT:
                return BasicType.INT;
            case LONG:
                return BasicType.LONG;
            case OBJECT:
                return BasicType.OBJECT;
            case SHORT:
                return BasicType.SHORT;
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }
    }

}
