package com.badoo.hprof.library.model;

import java.util.Arrays;

/**
 * Class containing the data of a class instance dump (INSTANCE_DUMP) heap record.
 *
 * Created by Erik Andre on 17/07/2014.
 */
public class Instance {

    private int objectId;
    private int stackTraceSerialId;
    private int classObjectId;
    private byte[] instanceFieldData;

    public Instance(int objectId, int stackTraceSerialId, int classObjectId, byte[] instanceFieldData) {
        this.objectId = objectId;
        this.stackTraceSerialId = stackTraceSerialId;
        this.classObjectId = classObjectId;
        this.instanceFieldData = instanceFieldData;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public int getStackTraceSerialId() {
        return stackTraceSerialId;
    }

    public void setStackTraceSerialId(int stackTraceSerialId) {
        this.stackTraceSerialId = stackTraceSerialId;
    }

    public int getClassObjectId() {
        return classObjectId;
    }

    public void setClassObjectId(int classObjectId) {
        this.classObjectId = classObjectId;
    }

    public byte[] getInstanceFieldData() {
        return instanceFieldData;
    }

    public void setInstanceFieldData(byte[] instanceFieldData) {
        this.instanceFieldData = instanceFieldData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Instance instance = (Instance) o;

        if (classObjectId != instance.classObjectId) return false;
        if (objectId != instance.objectId) return false;
        if (stackTraceSerialId != instance.stackTraceSerialId) return false;
        if (!Arrays.equals(instanceFieldData, instance.instanceFieldData)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = objectId;
        result = 31 * result + stackTraceSerialId;
        result = 31 * result + classObjectId;
        result = 31 * result + Arrays.hashCode(instanceFieldData);
        return result;
    }
}
