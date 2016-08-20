package com.badoo.bmd.decruncher;

import com.badoo.bmd.BmdProcessor;
import com.badoo.bmd.BmdReader;
import com.badoo.bmd.BmdTag;
import com.badoo.bmd.model.BmdBasicType;
import com.badoo.bmd.model.BmdClassDefinition;
import com.badoo.bmd.model.BmdConstantField;
import com.badoo.bmd.model.BmdInstanceDump;
import com.badoo.bmd.model.BmdInstanceDumpField;
import com.badoo.bmd.model.BmdInstanceFieldDefinition;
import com.badoo.bmd.model.BmdLegacyRecord;
import com.badoo.bmd.model.BmdObjectArray;
import com.badoo.bmd.model.BmdPrimitiveArray;
import com.badoo.bmd.model.BmdStaticField;
import com.badoo.bmd.model.BmdString;
import com.badoo.hprof.library.HprofWriter;
import com.badoo.hprof.library.Tag;
import com.badoo.hprof.library.heap.HeapDumpWriter;
import com.badoo.hprof.library.model.*;
import com.badoo.hprof.library.util.StreamUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import static com.badoo.hprof.library.util.StreamUtil.write;
import static com.badoo.hprof.library.util.StreamUtil.writeByte;
import static com.badoo.hprof.library.util.StreamUtil.writeInt;
import static com.badoo.hprof.library.util.StreamUtil.writeLong;
import static com.badoo.hprof.library.util.StreamUtil.writeShort;

/**
 * A BmdProcessor implementation that converts BMD files to HPROF.
 * Since BMD contains less data than HPROF some filler data is generated in the process.
 * <p/>
 * - Contents of all primitive arrays is filled with zeros.
 * - Primitive instance fields are recreated (to keep the instance size consistent) as ints and bytes but without a real name
 * and with zero as content.
 * - Protection domains, stack trace serials and class loader ids are replaced with 0.
 * <p/>
 * Created by Erik Andre on 02/11/14.
 */
public class DecrunchProcessor implements BmdProcessor {

    private static final boolean DEBUG = false;
    private static final int FILLER_FIELD_NAME = Integer.MAX_VALUE;
    private final InstanceField BYTE_FILLER_FIELD;
    private final InstanceField INT_FILLER_FIELD;

    private final HprofWriter writer;
    private final Map<Integer, BmdClassDefinition> classes = new HashMap<Integer, BmdClassDefinition>();
    private final Map<Integer, String> strings;
    private final List<BmdInstanceDump> instances = new ArrayList<BmdInstanceDump>();
    private final List<BmdObjectArray> objectArrays = new ArrayList<BmdObjectArray>();
    private final List<BmdPrimitiveArray> primitiveArrays = new ArrayList<BmdPrimitiveArray>();
    private final List<Integer> rootObjects = new ArrayList<Integer>();

    /**
     * Create a new DecrunchProcessor.
     *
     * @param out     The output to write the decrunched data to
     * @param strings A set of strings from the original app, used to recover strings replaced by hashes in the dump
     */
    public DecrunchProcessor(@Nonnull OutputStream out, @Nonnull Set<String> strings) {
        writer = new HprofWriter(out);
        this.strings = new HashMap<Integer, String>();
        for (String string : strings) {
            int hashCode = string.hashCode();
            if (this.strings.containsKey(hashCode)) {
                System.err.println("WARN: String input contains strings which maps to the same hash code!");
                System.err.println("WARN: First: " + this.strings.get(hashCode) + ", second: " + string + ", hash: " + hashCode);
            }
            this.strings.put(hashCode, string);
        }
        StreamUtil.ID_SIZE = 4;
        BYTE_FILLER_FIELD = new InstanceField(BasicType.BYTE, new ID(FILLER_FIELD_NAME));
        INT_FILLER_FIELD = new InstanceField(BasicType.INT, new ID(FILLER_FIELD_NAME));
    }

    @Override
    public void onRecord(BmdTag tag, @Nonnull BmdReader reader) throws IOException {
        if (DEBUG) {
            System.out.println("Record: " + tag);
        }
        switch (tag) {
            case STRING:
                writeString(reader.readStringRecord());
                break;
            case HASHED_STRING:
                writeString(reader.readHashedStringRecord());
                break;
            case CLASS_DEFINITION:
                BmdClassDefinition classDef = reader.readClassDefinitionRecord();
                classes.put(classDef.getId(), classDef);
                // Write the load class record now and the class dump later
                writeLoadClassRecord(classDef);
                break;
            case INSTANCE_DUMP:
                instances.add(reader.readInstanceDumpRecord(classes));
                break;
            case OBJECT_ARRAY:
                objectArrays.add(reader.readObjectArrayRecord());
                break;
            case PRIMITIVE_ARRAY_PLACEHOLDER:
                primitiveArrays.add(reader.readPrimitiveArrayRecord());
                break;
            case ROOT_OBJECTS:
                int rootCount = reader.readInt32();
                for (int i = 0; i < rootCount; i++) {
                    rootObjects.add(reader.readInt32());
                }
                break;
            case LEGACY_HPROF_RECORD:
                BmdLegacyRecord legacyRecord = reader.readLegacyRecord();
                writeLegacyRecord(legacyRecord);
                break;
        }
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
        for (BmdInstanceDump instance : instances) {
            writeInstanceDump(heapWriter, instance);
        }
        // Object arrays
        for (BmdObjectArray array : objectArrays) {
            writeObjectArray(heapWriter, array);
        }
        // Primitive arrays
        for (BmdPrimitiveArray array : primitiveArrays) {
            writePrimitiveArray(heapWriter, array);
        }
        // GC roots
        for (Integer root : rootObjects) {
            heapWriter.writeUnknownRoot(new ID(root));
        }
        writer.writeStringRecord(new HprofString(new ID(FILLER_FIELD_NAME), "field_removed", 0)); // This string is used as the name for all discarded instance fields that we have recreated
        byte[] record = buffer.toByteArray();
        writer.writeRecordHeader(Tag.HEAP_DUMP, 0, record.length);
        writer.getOutputStream().write(record);
    }

    private ClassDefinition convertClassDefinition(BmdClassDefinition bmdClassDef) throws IOException {
        ClassDefinition classDef = new ClassDefinition();
        classDef.setObjectId(new ID(bmdClassDef.getId()));
        classDef.setNameStringId(new ID(bmdClassDef.getName()));
        classDef.setSuperClassObjectId(new ID(bmdClassDef.getSuperClassId()));
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
            staticFields.add(new StaticField(type, value, new ID(field.getNameId())));
        }
        classDef.setStaticFields(staticFields);
        // Instance fields
        List<InstanceField> instanceFields = new ArrayList<InstanceField>();
        for (BmdInstanceFieldDefinition field : bmdClassDef.getInstanceFields()) {
            BasicType type = convertType(field.getType());
            instanceFields.add(new InstanceField(type, new ID(field.getNameId())));
        }
        // Add extra instance fields replacing removed fields
        int filler = bmdClassDef.getDiscardedFieldSize();
        while (filler > 0) {
            InstanceField fillerField = filler >= 4 ? INT_FILLER_FIELD : BYTE_FILLER_FIELD;
            instanceFields.add(fillerField);
            filler -= fillerField.getType().size;
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

    private void writePrimitiveArray(HeapDumpWriter writer, BmdPrimitiveArray array) throws IOException {
        BasicType elementType = convertType(array.getType());
        writer.writePrimitiveArrayHeader(new ID(array.getId()), 0, elementType, array.getElementCount());
        // Just write 0's to fill the space
        byte[] data = new byte[elementType.size * array.getElementCount()];
        writer.getOutputStream().write(data);
    }

    private void writeObjectArray(HeapDumpWriter writer, BmdObjectArray array) throws IOException {
        writer.writeObjectArray(new ID(array.getId()), 0, new ID(array.getElementClassId()), toIDArray(array.getElements()));
    }
    private ID[] toIDArray(int[] is){
        final ID[] res = new ID[is.length];
        for (int i = 0; i < is.length; i++) {
            int id = is[i];
            res[i] = new ID(id);
        }
        return res;
    }

    private void writeInstanceDump(HeapDumpWriter writer, BmdInstanceDump instance) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int currentClassId = instance.getClassId();
        while (currentClassId != 0) {
            BmdClassDefinition currentClass = classes.get(currentClassId);
            // Write all fields for this class
            for (BmdInstanceDumpField field : instance.getFields()) {
                if (field.getClassDefinition().equals(currentClass)) {
                    writeFieldValue(buffer, field.getData());
                }
            }
            // Write filler data for removed fields
            if (currentClass.getDiscardedFieldSize() > 0) {
                write(buffer, 0, currentClass.getDiscardedFieldSize());
            }
            currentClassId = currentClass.getSuperClassId();
        }
        byte[] data = buffer.toByteArray();
        writer.writeInstanceDumpRecord(new ID(instance.getId()), 0, new ID(instance.getClassId()), data);
    }

    private void writeFieldValue(OutputStream out, Object value) throws IOException {
        if (value instanceof Boolean) {
            writeByte(out, (Boolean) value ? 1 : 0);
        }
        else if (value instanceof Byte) {
            writeByte(out, (Byte) value);
        }
        else if (value instanceof Character) {
            int intValue = (Character) value;
            writeShort(out, (short) intValue);
        }
        else if (value instanceof Double) {
            writeLong(out, Double.doubleToRawLongBits((Double) value));
        }
        else if (value instanceof Integer) {
            writeInt(out, (Integer) value);
        }
        else if (value instanceof Float) {
            writeInt(out, Float.floatToRawIntBits((Float) value));
        }
        else if (value instanceof Long) {
            writeLong(out, (Long) value);
        }
        else if (value instanceof Short) {
            writeShort(out, (Short) value);
        }
    }

    private void writeLegacyRecord(BmdLegacyRecord record) throws IOException {
        writer.writeRecordHeader(record.getOriginalTag(), 0, record.getData().length);
        writer.getOutputStream().write(record.getData());
    }

    private void writeLoadClassRecord(BmdClassDefinition classDef) throws IOException {
        ClassDefinition hprofCls = new ClassDefinition();
        hprofCls.setObjectId(new ID(classDef.getId()));
        hprofCls.setNameStringId(new ID(classDef.getName()));
        writer.writeLoadClassRecord(hprofCls);
    }

    private void writeString(BmdString string) throws IOException {
        String stringVal = string.getString();
        if (stringVal == null) { // Check if we can recover the string using the hash code
            stringVal = strings.get(string.getHash());
        }
        if (stringVal == null) { // Fall back to using a placeholder
            stringVal = "Hash$" + string.getHash();
            byte[] data = stringVal.getBytes();
            if (data.length < string.getLength()) {
                // Need to add filler to get back to the correct length
                byte[] filler = new byte[string.getLength() - data.length];
                Arrays.fill(filler, (byte) 43); // Fill with '+' signs
                stringVal += new String(filler, "UTF-8");
            }
        }
        HprofString hprofString = new HprofString(new ID(string.getId()), stringVal, 0);
        writer.writeStringRecord(hprofString);
    }

    @Override
    public void onHeader(int version, @Nonnull byte[] data) throws IOException {
        System.out.println("Header version:" + version + ", data=" + new String(data));
        writer.writeHprofFileHeader(new String(data), 4, 0, 0);
    }

    private byte[] getFieldValue(BmdConstantField field) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        writeFieldValue(buffer, field.getValue());
        return buffer.toByteArray();
    }

    private byte[] getFieldValue(BmdStaticField field) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        writeFieldValue(buffer, field.getValue());
        return buffer.toByteArray();
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
