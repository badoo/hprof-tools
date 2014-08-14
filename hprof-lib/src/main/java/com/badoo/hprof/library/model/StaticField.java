package com.badoo.hprof.library.model;

/**
 * Created by erikandre on 24/06/2014.
 */
public class StaticField {

    private final BasicType type;

    private final byte[] value;

    private final int fieldNameId;

    public StaticField(BasicType type, byte[] value, int fieldNameId) {
        this.type = type;
        this.value = value;
        this.fieldNameId = fieldNameId;
    }
}
