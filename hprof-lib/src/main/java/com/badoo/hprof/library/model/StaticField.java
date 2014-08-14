package com.badoo.hprof.library.model;

/**
 * Created by Erik Andre on 24/06/2014.
 */
public class StaticField implements NamedField{

    private BasicType type;

    private byte[] value;

    private int fieldNameId;

    public StaticField(BasicType type, byte[] value, int fieldNameId) {
        this.type = type;
        this.value = value;
        this.fieldNameId = fieldNameId;
    }

    public BasicType getType() {
        return type;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public int getFieldNameId() {
        return fieldNameId;
    }

    public void setType(BasicType type) {
        this.type = type;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    @Override
    public void setFieldNameId(int fieldNameId) {
        this.fieldNameId = fieldNameId;
    }
}
