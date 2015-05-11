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

import javax.annotation.Nonnull;

/**
 * Class for reading BMD files.
 * <p/>
 * Records of a BMD file are read sequentially by calling next(). This will result in a callback to the BmpProcessor implementation
 * provided in the constructor of the class.
 * <p/>
 * BmdProcessor proc = new MyProcessor();
 * BmdReader reader = new BmdReader(in, proc);
 * while (reader.hasNext()) {
 * reader.next();
 * }
 * <p/>
 * Created by Erik Andre on 02/11/14.
 */
public class BmdReader extends DataReader {

    private final BmdProcessor processor;
    private boolean readHeader = true;

    public BmdReader(@Nonnull InputStream in, @Nonnull BmdProcessor processor) {
        super(in);
        this.processor = processor;
    }

    /**
     * Returns true if there are more records to read.
     *
     * @return True if there are more records, otherwise false.
     */
    public boolean hasNext() throws IOException {
        return (in.available() > 0);
    }

    /**
     * Read the next record. This will result in a callback to the processor.
     */
    public void next() throws IOException {
        if (readHeader) {
            readHeader();
            readHeader = false;
        }
        else {
            readRecord();
        }
    }

    /**
     * Reads a BmdString containing real string data from the input.
     *
     * @return A BmdString
     */
    @Nonnull
    public BmdString readString() throws IOException {
        int id = readInt32();
        int length = readInt32();
        byte[] data = readRawBytes(length);
        return new BmdString(id, new String(data));
    }

    /**
     * Reads a BmdString containing a string hash from the input.
     *
     * @return A BmdString
     */
    @Nonnull
    public BmdString readHashedString() throws IOException {
        int id = readInt32();
        int length = readInt32();
        int hash = readInt32();
        return new BmdString(id, length, hash);
    }

    /**
     * Reads a class definition from the input.
     *
     * @return A BmdClassDefinition
     */
    @Nonnull
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
        int instanceCount = readInt32();
        List<BmdInstanceFieldDefinition> instanceFields = new ArrayList<BmdInstanceFieldDefinition>();
        for (int i = 0; i < instanceCount; i++) {
            instanceFields.add(readInstanceField());
        }
        int discardedFieldSize = readInt32();
        return new BmdClassDefinition(classId, superClassId, name, constantFields, staticFields, instanceFields, discardedFieldSize);
    }

    /**
     * Reads an object array from the input.
     *
     * @return An BmdObjectArray
     */
    @Nonnull
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

    /**
     * Reads a primitive array (without array data) from the input.
     *
     * @return A BmdPrimitiveArray
     */
    @Nonnull
    public BmdPrimitiveArray readPrimitiveArray() throws IOException {
        int objectId = readInt32();
        BmdBasicType type = BmdBasicType.fromInt(readInt32());
        int count = readInt32();
        return new BmdPrimitiveArray(objectId, type, count);
    }

    /**
     * Reads an instance dump from the input. All classes in the instance inheritance hierarchy must already have been loaded.
     *
     * @param classes A mapping of class ids to loaded class definitions
     * @return A BmdInstanceDump
     */
    @Nonnull
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
                        fields.add(new BmdInstanceDumpField(currentClass, field, readInt32()));
                        break;
                    case LONG:
                        fields.add(new BmdInstanceDumpField(currentClass, field, readInt64()));
                        break;
                    case DOUBLE:
                        fields.add(new BmdInstanceDumpField(currentClass, field, readDouble()));
                        break;
                    case FLOAT:
                        fields.add(new BmdInstanceDumpField(currentClass, field, readFloat()));
                        break;
                    case BOOLEAN:
                        fields.add(new BmdInstanceDumpField(currentClass, field, readRawByte() != 0));
                        break;
                    case BYTE:
                        fields.add(new BmdInstanceDumpField(currentClass, field, readRawByte()));
                        break;
                    case CHAR:
                        fields.add(new BmdInstanceDumpField(currentClass, field, Chars.fromBytes(readRawByte(), readRawByte())));
                        break;

                }
            }
            currentClass = classes.get(currentClass.getSuperClassId());
        }
        return new BmdInstanceDump(objectId, classDef.getId(), fields);
    }

    /**
     * Reads a legacy record containing HPROF data.
     *
     * @return a BmdLegacyRecord
     */
    @Nonnull
    public BmdLegacyRecord readLegacyRecord() throws IOException {
        int tag = readInt32();
        int length = readInt32();
        byte[] data = readRawBytes(length);
        return new BmdLegacyRecord(tag, data);
    }

    private BmdInstanceFieldDefinition readInstanceField() throws IOException {
        int nameId = readInt32();
        BmdBasicType type = BmdBasicType.fromInt(readInt32());
        return new BmdInstanceFieldDefinition(nameId, type);
    }

    private BmdStaticField readStaticField() throws IOException {
        int nameId = readInt32();
        BmdBasicType type = BmdBasicType.fromInt(readInt32());
        Object value;
        switch (type) {
            case OBJECT:
            case INT:
                value = readInt32();
                break;
            case BOOLEAN:
                value = readBool();
                break;
            case BYTE:
                value = readRawByte();
                break;
            case CHAR:
                value = Chars.fromByteArray(readRawBytes(2));
                break;
            case FLOAT:
                value = readFloat();
                break;
            case DOUBLE:
                value = readDouble();
                break;
            case LONG:
                value = readInt64();
                break;
            case SHORT:
                value = (short) readInt32();
                break;
            default:
                throw new IllegalArgumentException("Invalid field type: " + type);
        }
        return new BmdStaticField(nameId, type, value);
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
}
