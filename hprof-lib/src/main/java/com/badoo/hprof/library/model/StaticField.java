package com.badoo.hprof.library.model;

import java.util.Arrays;

import javax.annotation.Nonnull;

/**
 * Created by Erik Andre on 24/06/2014.
 */
public class StaticField implements NamedField {

    private BasicType type;

    private byte[] value;

    private ID fieldNameId;

    public StaticField(@Nonnull BasicType type, @Nonnull byte[] value, ID fieldNameId) {
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
    public ID getFieldNameId() {
        return fieldNameId;
    }

    @Override
    public void setFieldNameId(ID fieldNameId) {
        this.fieldNameId = fieldNameId;
    }

    @Override
    public String toString() {
        return "StaticField{" +
                "type=" + type +
                ", value=" + Arrays.toString(value) +
                ", fieldNameId=" + fieldNameId +
                '}';
    }
}
