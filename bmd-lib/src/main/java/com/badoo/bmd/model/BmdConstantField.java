package com.badoo.bmd.model;

/**
 * Data class for constant fields in BMD class definitions.
 *
 * Created by Erik Andre on 23/10/14.
 */
public class BmdConstantField {

    private final int index;
    private final BmdBasicType type;
    private final Object value;

    public BmdConstantField(int index, BmdBasicType type, Object value) {
        this.index = index;
        this.type = type;
        this.value = value;
    }

    public BmdBasicType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }
}
