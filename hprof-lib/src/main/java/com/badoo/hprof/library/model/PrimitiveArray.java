package com.badoo.hprof.library.model;

import java.util.Arrays;

import javax.annotation.Nonnull;

/**
 * Model class for HPROF primitive arrays.
 *
 * Created by Erik Andre on 23/04/15.
 */
public class PrimitiveArray {

    private final ID objectId;
    private final int stackTraceSerial;
    private final BasicType type;
    private final int count;
    private final byte[] arrayData;

    public PrimitiveArray(ID objectId, int stackTraceSerial, @Nonnull BasicType type, int count, @Nonnull byte[] arrayData) {
        this.objectId = objectId;
        this.stackTraceSerial = stackTraceSerial;
        this.type = type;
        this.count = count;
        this.arrayData = arrayData;
    }

    public ID getObjectId() {
        return objectId;
    }

    public int getStackTraceSerial() {
        return stackTraceSerial;
    }

    public BasicType getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public byte[] getArrayData() {
        return arrayData;
    }


    @Override
    public String toString() {
        return "PrimitiveArray{" +
                "objectId=" + objectId +
                ", stackTraceSerial=" + stackTraceSerial +
                ", type=" + type +
                ", count=" + count +
                ", arrayData=" + Arrays.toString(arrayData) +
                '}';
    }
}
