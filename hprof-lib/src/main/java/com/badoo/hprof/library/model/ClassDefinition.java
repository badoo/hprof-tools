package com.badoo.hprof.library.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model for a Java class. ClassDefinition contains data from two different hprof records, LOAD_CLASS and CLASS_DUMP (Heap dump record).
 * <p/>
 * Created by Erik Andre on 17/07/2014.
 */
public class ClassDefinition extends Record {

    // Fields from LOAD_CLASS
    private int serialNumber;
    private int objectId;
    private int nameStringId;
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
        return constantFields != null ? constantFields : Collections.<ConstantField>emptyList();
    }

    public void setConstantFields(List<ConstantField> constantFields) {
        this.constantFields = constantFields;
    }

    public List<StaticField> getStaticFields() {
        return staticFields != null ? staticFields : Collections.<StaticField>emptyList();
    }

    public void setStaticFields(List<StaticField> staticFields) {
        this.staticFields = staticFields;
    }

    public List<InstanceField> getInstanceFields() {
        return instanceFields != null ? instanceFields : Collections.<InstanceField>emptyList();
    }

    public void setInstanceFields(List<InstanceField> instanceFields) {
        this.instanceFields = instanceFields;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public void setNameStringId(int nameStringId) {
        this.nameStringId = nameStringId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassDefinition that = (ClassDefinition) o;

        if (classLoaderObjectId != that.classLoaderObjectId) return false;
        if (instanceSize != that.instanceSize) return false;
        if (nameStringId != that.nameStringId) return false;
        if (objectId != that.objectId) return false;
        if (protectionDomainObjectId != that.protectionDomainObjectId) return false;
        if (serialNumber != that.serialNumber) return false;
        if (signersObjectId != that.signersObjectId) return false;
        if (stackTraceSerial != that.stackTraceSerial) return false;
        if (superClassObjectId != that.superClassObjectId) return false;
        if (constantFields != null ? !constantFields.equals(that.constantFields) : that.constantFields != null) return false;
        if (instanceFields != null ? !instanceFields.equals(that.instanceFields) : that.instanceFields != null) return false;
        if (staticFields != null ? !staticFields.equals(that.staticFields) : that.staticFields != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = serialNumber;
        result = 31 * result + objectId;
        result = 31 * result + nameStringId;
        result = 31 * result + stackTraceSerial;
        result = 31 * result + superClassObjectId;
        result = 31 * result + classLoaderObjectId;
        result = 31 * result + signersObjectId;
        result = 31 * result + protectionDomainObjectId;
        result = 31 * result + instanceSize;
        result = 31 * result + (constantFields != null ? constantFields.hashCode() : 0);
        result = 31 * result + (staticFields != null ? staticFields.hashCode() : 0);
        result = 31 * result + (instanceFields != null ? instanceFields.hashCode() : 0);
        return result;
    }
}
