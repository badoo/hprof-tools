package com.badoo.hprof.library.model;

import javax.annotation.Nonnull;

/**
 * Model class for HPROF object arrays.
 *
 * Created by Erik Andre on 23/04/15.
 */
public class ObjectArray {

    private final int objectId;
    private final int stackTraceSerial;
    private final int elementClassId;
    private final int count;
    private final int[] elements;

    public ObjectArray(int objectId, int stackTraceSerial, int elementClassId, int count, @Nonnull int[] elements) {
        this.objectId = objectId;
        this.stackTraceSerial = stackTraceSerial;
        this.elementClassId = elementClassId;
        this.count = count;
        this.elements = elements;
    }

    public int getObjectId() {
        return objectId;
    }

    public int getStackTraceSerial() {
        return stackTraceSerial;
    }

    public int getElementClassId() {
        return elementClassId;
    }

    public int getCount() {
        return count;
    }

    public int[] getElements() {
        return elements;
    }
}
