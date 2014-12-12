package com.badoo.bmd;

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
import com.google.common.primitives.Chars;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * TODO: Document
 * Created by Erik Andre on 02/11/14.
 */
public class BmdReader extends DataReader {

    private final BmdProcessor processor;
    private boolean readHeader = true;

    public BmdReader(InputStream in, BmdProcessor processor) {
        super(in);
        this.processor = processor;
    }

    public InputStream getInputStream() {
        return in;
    }

    public boolean hasNext() throws IOException {
        return (in.available() > 0);
    }

    public void next() throws IOException {
        if (readHeader) {
            readHeader();
            readHeader = false;
        }
        else {
            readRecord();
        }
    }

    public BmdString readString() throws IOException {
        int id = readInt32();
        int length = readInt32();
        byte[] data = readRawBytes(length);
        return new BmdString(id, new String(data));
    }

    public BmdString readHashedString() throws IOException {
        int id = readInt32();
        int hash = readInt32();
        return new BmdString(id, hash);
    }

    public BmdClassDefinition readClassDefinition() throws IOException {
        int classId = readInt32();
        int superClassId = readInt32();
        int name = readInt32();
        int constantCount = readInt32();
        List<BmdConstantField> constantFields = new ArrayList<BmdConstantField>();
        for (int i = 0; i < constantCount; i++) {
            constantFields.add(readConstantField());
        }
        int staticCount = readInt32();
        List<BmdStaticField> staticFields = new ArrayList<BmdStaticField>();
        for (int i = 0; i < staticCount; i++) {
            staticFields.add(readStaticField());
        }
        List<BmdInstanceFieldDefinition> instanceFields = new ArrayList<BmdInstanceFieldDefinition>();
        BmdInstanceFieldDefinition field = readInstanceField();
        while (field != null) {
            instanceFields.add(field);
            field = readInstanceField(); // Read the next field
        }
        int discardedFieldSize = readInt32();
        return new BmdClassDefinition(classId, superClassId, name, constantFields, staticFields, instanceFields, discardedFieldSize);
    }

    private BmdInstanceFieldDefinition readInstanceField() throws IOException {
        int nameId = readInt32();
        if (nameId == 0) {
            return null;
        }
        BmdBasicType type = BmdBasicType.fromInt(readInt32());
        return new BmdInstanceFieldDefinition(nameId, type);
    }

    private BmdStaticField readStaticField() throws IOException {
        int nameId = readInt32();
        BmdBasicType type = BmdBasicType.fromInt(readInt32());
        BmdStaticField field = new BmdStaticField(nameId, type);
        switch (type) {
            case OBJECT:
            case INT:
                field.setIntValue(readInt32());
                break;
            case BOOLEAN:
                field.setBoolValue(readBool());
                break;
            case BYTE:
                field.setByteValue(readRawByte());
                break;
            case CHAR:
                field.setCharValue(Chars.fromByteArray(readRawBytes(2)));
                break;
            case FLOAT:
                field.setFloatValue(readFloat());
                break;
            case DOUBLE:
                field.setDoubleValue(readDouble());
                break;
            case LONG:
                field.setLongValue(readInt64());
                break;
            case SHORT:
                field.setShortValue((short) readInt32());
                break;
            default:
                throw new IllegalArgumentException("Invalid field type: " + type);
        }
        return field;
    }

    private BmdConstantField readConstantField() throws IOException {
        int index = readInt32();
        BmdBasicType type = BmdBasicType.fromInt(readInt32());
        switch (type) {
            case OBJECT:
            case INT:
                return new BmdConstantField(index, type, readInt32());
            case BOOLEAN:
                return new BmdConstantField(index, type, readBool());
            case BYTE:
                return new BmdConstantField(index, type, readRawByte());
            case CHAR:
                return new BmdConstantField(index, type, Chars.fromByteArray(readRawBytes(2)));
            case FLOAT:
                return new BmdConstantField(index, type, readFloat());
            case DOUBLE:
                return new BmdConstantField(index, type, readDouble());
            case LONG:
                return new BmdConstantField(index, type, readInt64());
            case SHORT:
                return new BmdConstantField(index, type, (short) readInt32());
            default:
                throw new IllegalArgumentException("Invalid field type: " + type);
        }
    }

    private void readRecord() throws IOException {
        int tag = readInt32();
        processor.onRecord(tag, this);
    }

    private void readHeader() throws IOException {
        int version = readInt32();
        int metaDataLength = readInt32();
        byte[] metadata = readRawBytes(metaDataLength);
        processor.onHeader(version, metadata);
    }

    public BmdObjectArray readObjectArray() throws IOException {
        int objectId = readInt32();
        int elementClassId = readInt32();
        int count = readInt32();
        int[] elements = new int[count];
        for (int i = 0; i < count; i++) {
            elements[i] = readInt32();
        }
        return new BmdObjectArray(objectId, elementClassId, elements);
    }

    public BmdPrimitiveArray readPrimitiveArray() throws IOException {
        int objectId = readInt32();
        BmdBasicType type = BmdBasicType.fromInt(readInt32());
        int count = readInt32();
        return new BmdPrimitiveArray(objectId, type, count);
    }

    public BmdInstanceDump readInstanceDump(Map<Integer, BmdClassDefinition> classes) throws IOException {
        int objectId = readInt32();
        int classId = readInt32();
        BmdClassDefinition classDef = classes.get(classId);
        if (classDef == null) {
            throw new IllegalStateException("No class loaded with id: " + classId);
        }
        // Calculate instance size
        BmdClassDefinition currentClass = classDef;
        List<BmdInstanceDumpField> fields = new ArrayList<BmdInstanceDumpField>();
        while (currentClass != null) {
            for (BmdInstanceFieldDefinition field : currentClass.getInstanceFields()) {
                switch (field.getType()) {
                    case OBJECT:
                    case INT:
                    case SHORT:
                        fields.add(new BmdInstanceDumpField(field, readInt32()));
                        break;
                    case LONG:
                        fields.add(new BmdInstanceDumpField(field, readInt64()));
                        break;
                    case DOUBLE:
                        fields.add(new BmdInstanceDumpField(field, readDouble()));
                        break;
                    case FLOAT:
                        fields.add(new BmdInstanceDumpField(field, readFloat()));
                        break;
                    case BOOLEAN:
                        fields.add(new BmdInstanceDumpField(field, readRawByte() != 0));
                        break;
                    case BYTE:
                        fields.add(new BmdInstanceDumpField(field, readRawByte()));
                        break;
                    case CHAR:
                        fields.add(new BmdInstanceDumpField(field, Chars.fromBytes(readRawByte(), readRawByte())));
                        break;

                }
            }
            currentClass = classes.get(currentClass.getSuperClassId());
        }
        return new BmdInstanceDump(objectId, classDef.getId(), fields);
    }

    public BmdLegacyRecord readLegacyRecord() throws IOException {
        int tag = readInt32();
        int length = readInt32();
        byte[] data = readRawBytes(length);
        return new BmdLegacyRecord(tag, data);
    }
}
