package com.badoo.hprof.library.model;

import javax.annotation.Nonnull;

/**
 * Created by Erik Andre on 24/06/2014.
 */
public class StaticField implements NamedField {

    private BasicType type;

    private byte[] value;

    private int fieldNameId;

    public StaticField(@Nonnull BasicType type, @Nonnull byte[] value, int fieldNameId) {
        this.type = type;
        this.value = value;
        this.fieldNameId = fieldNameId;
    }

    @Nonnull
    public BasicType getType() {
        return type;
    }

    public void setType(@Nonnull BasicType type) {
        this.type = type;
    }

    @Nonnull
    public byte[] getValue() {
        return value;
    }

    public void setValue(@Nonnull byte[] value) {
        this.value = value;
    }

    @Override
    public int getFieldNameId() {
        return fieldNameId;
    }

    @Override
    public void setFieldNameId(int fieldNameId) {
        this.fieldNameId = fieldNameId;
    }
}
