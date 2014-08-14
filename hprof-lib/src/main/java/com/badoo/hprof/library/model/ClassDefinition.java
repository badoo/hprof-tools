package com.badoo.hprof.library.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.badoo.hprof.library.IoUtil.readInt;
import static com.badoo.hprof.library.IoUtil.readShort;

/**
 * Created by Erik Andre on 17/07/2014.
 */
public class ClassDefinition {

    // Fields from LOAD_CLASS
    private final int serialNumber;
    private final int objectId;
    private int stackTraceSerial;
    private final int nameStringId;

    // Fiends from CLASS_DUMP
    private int superClassObjectId;
    private int classLoaderObjectId;
    private int signersObjectId;
    private int protectionDomainObjectId;
    private int instanceSize;
    private List<ConstantField> constantFields;
    private List<StaticField> staticFields;
    private List<InstanceField> instanceFields;

    public static ClassDefinition createFromLoadClassData(InputStream in) throws IOException {
        int serialNumber = readInt(in);
        int classObjectId = readInt(in);
        int stackTraceSerial = readInt(in);
        int classNameStringId = readInt(in);
        return new ClassDefinition(serialNumber, classObjectId, stackTraceSerial, classNameStringId);
    }

    public ClassDefinition(int serialNumber, int objectId, int stackTraceSerial, int nameStringId) {
        this.serialNumber = serialNumber;
        this.objectId = objectId;
        this.stackTraceSerial = stackTraceSerial;
        this.nameStringId = nameStringId;
    }

    /**
     * Populate the fields of this ClassDefinition using data from a CLASS_DUMP record. It is assumed that the object id has already been read.
     *
     * @param in Input stream positioned at the "stack trace serial number" field of the CLASS_DUMP
     * @throws IOException
     */
    public void populateFromClassDump(InputStream in) throws IOException {
        stackTraceSerial = readInt(in);
        superClassObjectId = readInt(in);
        classLoaderObjectId = readInt(in);
        signersObjectId = readInt(in);
        protectionDomainObjectId = readInt(in);
        in.skip(8); // Reserved data
        instanceSize = readInt(in);

        short constantCount = readShort(in);
        if (constantCount > 0) {
            constantFields = new ArrayList<ConstantField>();
        }
        for (int i = 0; i < constantCount; i++) {
            short poolIndex = readShort(in);
            BasicType type = BasicType.fromType(in.read());
            byte[] value = new byte[type.size];
            in.read(value);
            constantFields.add(new ConstantField(poolIndex, type, value));

        }
        short staticCount = readShort(in);
        if (staticCount > 0) {
            staticFields = new ArrayList<StaticField>();
        }
        for (int i = 0; i < staticCount; i++) {
            int nameId = readInt(in);
            BasicType type = BasicType.fromType(in.read());
            byte[] value = new byte[type.size];
            in.read(value);
            staticFields.add(new StaticField(type, value, nameId));
        }
        short fieldCount = readShort(in);
        if (fieldCount > 0) {
            instanceFields = new ArrayList<InstanceField>();
        }
        for (int i = 0; i < fieldCount; i++) {
            int nameId = readInt(in);
            BasicType type = BasicType.fromType(in.read());
            instanceFields.add(new InstanceField(type, nameId));
        }
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public int getObjectId() {
        return objectId;
    }

    public int getStackTraceSerial() {
        return stackTraceSerial;
    }

    public int getNameStringId() {
        return nameStringId;
    }

    public int getSuperClassObjectId() {
        return superClassObjectId;
    }

    public void setSuperClassObjectId(int superClassObjectId) {
        this.superClassObjectId = superClassObjectId;
    }

    public int getClassLoaderObjectId() {
        return classLoaderObjectId;
    }

    public void setClassLoaderObjectId(int classLoaderObjectId) {
        this.classLoaderObjectId = classLoaderObjectId;
    }

    public int getSignersObjectId() {
        return signersObjectId;
    }

    public void setSignersObjectId(int signersObjectId) {
        this.signersObjectId = signersObjectId;
    }

    public int getProtectionDomainObjectId() {
        return protectionDomainObjectId;
    }

    public void setProtectionDomainObjectId(int protectionDomainObjectId) {
        this.protectionDomainObjectId = protectionDomainObjectId;
    }

    public int getInstanceSize() {
        return instanceSize;
    }

    public void setInstanceSize(int instanceSize) {
        this.instanceSize = instanceSize;
    }

    public void addConstantField(ConstantField field) {
        if (constantFields == null) {
            constantFields = new ArrayList<ConstantField>();
        }
        constantFields.add(field);
    }

    public void addStaticField(StaticField field) {
        if (staticFields == null) {
            staticFields = new ArrayList<StaticField>();
        }
        staticFields.add(field);
    }

    public void addInstanceField(InstanceField field) {
        if (instanceFields == null) {
            instanceFields = new ArrayList<InstanceField>();
        }
        instanceFields.add(field);
    }

    public List<ConstantField> getConstantFields() {
        return constantFields != null ? constantFields : Collections.EMPTY_LIST;
    }

    public List<StaticField> getStaticFields() {
        return staticFields != null ? staticFields : Collections.EMPTY_LIST;
    }

    public List<InstanceField> getInstanceFields() {
        return instanceFields != null ? instanceFields : Collections.EMPTY_LIST;
    }


}
