package com.badoo.hprof.library.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for a Java class.
 * <p/>
 * Created by Erik Andre on 17/07/2014.
 */
public class ClassDefinition extends Record {

    private static final List<ConstantField> NO_CONSTANT_FIELDS = new ArrayList<ConstantField>(0);
    private static final List<StaticField> NO_STATIC_FIELDS = new ArrayList<StaticField>(0);
    private static final List<InstanceField> NO_INSTANCE_FIELDS = new ArrayList<InstanceField>(0);


    // Fields from LOAD_CLASS
    private final int serialNumber;
    private final int objectId;
    private final int nameStringId;
    private int stackTraceSerial;
    // Fiends from CLASS_DUMP
    private int superClassObjectId;
    private int classLoaderObjectId;
    private int signersObjectId;
    private int protectionDomainObjectId;
    private int instanceSize;
    private List<ConstantField> constantFields;
    private List<StaticField> staticFields;
    private List<InstanceField> instanceFields;

    public ClassDefinition(int serialNumber, int objectId, int stackTraceSerial, int nameStringId) {
        this.serialNumber = serialNumber;
        this.objectId = objectId;
        this.stackTraceSerial = stackTraceSerial;
        this.nameStringId = nameStringId;
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

    public void setStackTraceSerial(int stackTraceSerial) {
        this.stackTraceSerial = stackTraceSerial;
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
        return constantFields != null ? constantFields : NO_CONSTANT_FIELDS;
    }

    public void setConstantFields(List<ConstantField> constantFields) {
        this.constantFields = constantFields;
    }

    public List<StaticField> getStaticFields() {
        return staticFields != null ? staticFields : NO_STATIC_FIELDS;
    }

    public void setStaticFields(List<StaticField> staticFields) {
        this.staticFields = staticFields;
    }

    public List<InstanceField> getInstanceFields() {
        return instanceFields != null ? instanceFields : NO_INSTANCE_FIELDS;
    }

    public void setInstanceFields(List<InstanceField> instanceFields) {
        this.instanceFields = instanceFields;
    }
}
