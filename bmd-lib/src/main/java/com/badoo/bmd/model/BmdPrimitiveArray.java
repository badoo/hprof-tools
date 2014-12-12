package com.badoo.bmd.model;

/**
 * Created by Erik Andre on 09/11/14.
 */
public class BmdPrimitiveArray {

    private final int id;
    private final BmdBasicType type;
    private final int elementCount;

    public BmdPrimitiveArray(int id, BmdBasicType type, int elementCount) {
        this.id = id;
        this.type = type;
        this.elementCount = elementCount;
    }

    public int getId() {
        return id;
    }

    public BmdBasicType getType() {
        return type;
    }

    public int getElementCount() {
        return elementCount;
    }
}
