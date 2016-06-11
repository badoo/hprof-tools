package com.badoo.hprof.library.model;

import com.badoo.hprof.library.util.StreamUtil;

import java.util.ArrayList;
import java.util.Arrays;
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
    private ID objectId;
    private ID nameStringId;
    private int stackTraceSerial;

    // Fields from CLASS_DUMP
    private ID superClassObjectId;
    private ID classLoaderObjectId;
    private ID signersObjectId;
    private ID protectionDomainObjectId;
    private int instanceSize;
    private List<ConstantField> constantFields;
    private List<StaticField> staticFields;
    private List<InstanceField> instanceFields;


    public int getSerialNumber() {
        return serialNumber;
    }

    public ID getObjectId() {
        return objectId;
    }

    public int getStackTraceSerial() {
        return stackTraceSerial;
    }

    public void setStackTraceSerial(int stackTraceSerial) {
        this.stackTraceSerial = stackTraceSerial;
    }

    public ID getNameStringId() {
        return nameStringId;
    }

    public ID getSuperClassObjectId() {
        return superClassObjectId;
    }

    public void setSuperClassObjectId(ID superClassObjectId) {
        this.superClassObjectId = superClassObjectId;
    }

    public ID getClassLoaderObjectId() {

        return classLoaderObjectId;
    }

    public void setClassLoaderObjectId(ID classLoaderObjectId) {
        this.classLoaderObjectId = classLoaderObjectId;
    }

    public ID getSignersObjectId() {
        return signersObjectId;
    }

    public void setSignersObjectId(ID signersObjectId) {
        this.signersObjectId = signersObjectId;
    }

    public ID getProtectionDomainObjectId() {
        return protectionDomainObjectId;
    }

    public void setProtectionDomainObjectId(ID protectionDomainObjectId) {
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

    public void setObjectId(ID objectId) {
        this.objectId = objectId;
    }

    public void setNameStringId(ID nameStringId) {
        this.nameStringId = nameStringId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassDefinition that = (ClassDefinition) o;


//        if (classLoaderObjectId != that.classLoaderObjectId) return false;
        if(!classLoaderObjectId.equals(that.classLoaderObjectId))return false;

        if (instanceSize != that.instanceSize) return false;
//        if (nameStringId != that.nameStringId) return false;
        if(!nameStringId.equals(that.nameStringId)) return false;
//        if (objectId != that.objectId) return false;
        if(!objectId.equals(that.objectId)) return false;

//        if (protectionDomainObjectId != that.protectionDomainObjectId) return false;
        if(!protectionDomainObjectId.equals(that.protectionDomainObjectId))return false;

        if (serialNumber != that.serialNumber) return false;
//        if (signersObjectId != that.signersObjectId) return false;
        if(signersObjectId.equals(that.signersObjectId))return false;
        if (stackTraceSerial != that.stackTraceSerial) return false;
//        if (superClassObjectId != that.superClassObjectId) return false;
        if (superClassObjectId.equals(that.superClassObjectId)) return false;


        if (constantFields != null ? !constantFields.equals(that.constantFields) : that.constantFields != null) return false;
        if (instanceFields != null ? !instanceFields.equals(that.instanceFields) : that.instanceFields != null) return false;
        if (staticFields != null ? !staticFields.equals(that.staticFields) : that.staticFields != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = serialNumber;


        int objIdInt=0;
        int nameStringIdInt=0;
        int superClassObjectIdInt=0;
        int classLoaderObjectIdInt=0;
        int signersObjectIdInt=0;
        int protectionDomainObjectIdInt=0;

        int maxIterations = Math.min(4,StreamUtil.ID_SIZE); // 4 for integer size
        for (int i=0;i< maxIterations;i++)
        {
            objIdInt |= objectId.getIdBytes()[i] << (8 * (maxIterations-i-1));
            nameStringIdInt |= nameStringId.getIdBytes()[i] << (8 * (maxIterations-i-1));
            superClassObjectIdInt |= superClassObjectId.getIdBytes()[i] << (8 * (maxIterations-i-1));
            classLoaderObjectIdInt |= classLoaderObjectId.getIdBytes()[i] << (8 * (maxIterations-i-1));
            signersObjectIdInt |= signersObjectId.getIdBytes()[i] << (8 * (maxIterations-i-1));
            protectionDomainObjectIdInt |= protectionDomainObjectId.getIdBytes()[i] << (8 * (maxIterations-i-1));

        }
        result = 31 * result + objIdInt;
        result = 31 * result + nameStringIdInt;
        result = 31 * result + stackTraceSerial;
        result = 31 * result + superClassObjectIdInt;
        result = 31 * result + classLoaderObjectIdInt;
        result = 31 * result + signersObjectIdInt;
        result = 31 * result + protectionDomainObjectIdInt;
        result = 31 * result + instanceSize;
        result = 31 * result + (constantFields != null ? constantFields.hashCode() : 0);
        result = 31 * result + (staticFields != null ? staticFields.hashCode() : 0);
        result = 31 * result + (instanceFields != null ? instanceFields.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClassDefinition{" +
                "serialNumber=" + serialNumber +
                ", objectId=" + objectId +
                ", nameStringId=" + nameStringId +
                ", stackTraceSerial=" + stackTraceSerial +
                ", superClassObjectId=" + superClassObjectId +
                ", classLoaderObjectId=" + classLoaderObjectId +
                ", signersObjectId=" + signersObjectId +
                ", protectionDomainObjectId=" + protectionDomainObjectId +
                ", instanceSize=" + instanceSize +
                ", constantFields=" + constantFields +
                ", staticFields=" + staticFields +
                ", instanceFields=" + instanceFields +
                '}';
    }
}
