package com.badoo.hprof.library.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Erik Andre on 17/07/2014.
 */
public class ClassDefinition {

    // Fields from LOAD_CLASS
    private final int serialNumber;
    private final int objectId;
    private final int stackTraceSerial;
    private final int nameStringId;

    // Fiends from CLASS_DUMP
    private int superClassObjectId;
    private int classLoaderObjectId;
    private int signersObjectId;
    private int protectionDomainObjectId;
    private int instanceSize;
    private List<ConstantField> constantFields;
    private List<StaticField> staticFields;
    private List<InstanceFieldInfo> instanceFields;


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

    public void addInstanceField(InstanceFieldInfo field) {
        if (instanceFields == null) {
            instanceFields = new ArrayList<InstanceFieldInfo>();
        }
        instanceFields.add(field);
    }

    public List<ConstantField> getConstantFields() {
        return constantFields != null? constantFields : Collections.EMPTY_LIST;
    }

    public List<StaticField> getStaticFields() {
        return staticFields != null? staticFields : Collections.EMPTY_LIST;
    }

    public List<InstanceFieldInfo> getInstanceFields() {
        return instanceFields != null? instanceFields : Collections.EMPTY_LIST;
    }


}
