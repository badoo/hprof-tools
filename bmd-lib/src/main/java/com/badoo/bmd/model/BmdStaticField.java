package com.badoo.bmd.model;

/**
 * Data class for static fields in BMD class definitions.
 * <p/>
 * Created by Erik Andre on 02/11/14.
 */
@SuppressWarnings("UnusedDeclaration")
public class BmdStaticField {

    private final int nameId;
    private final BmdBasicType type;
    private final Object value;

    public BmdStaticField(int nameId, BmdBasicType type, Object value) {
        this.nameId = nameId;
        this.type = type;
        this.value = value;
    }

    public int getNameId() {
        return nameId;
    }

    public BmdBasicType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}
