package com.badoo.hprof.library.model;

import javax.annotation.Nonnull;

/**
 * Class containing the definition of an instance field (part of CLASS_DUMP) but not the actual field value itself (part of INSTANCE_DUMP).
 * <p/>
 * Created by Erik Andre on 24/06/2014.
 */
public class InstanceField implements NamedField {

    private BasicType type;
    private int nameId;

    public InstanceField(@Nonnull BasicType type, int nameId) {
        this.type = type;
        this.nameId = nameId;
    }

    @Nonnull
    public BasicType getType() {
        return type;
    }

    public void setType(BasicType type) {
        this.type = type;
    }

    @Override
    public int getFieldNameId() {
        return nameId;
    }

    @Override
    public void setFieldNameId(int nameId) {
        this.nameId = nameId;
    }

    @Override
    public String toString() {
        return "InstanceField{" +
            "type=" + type +
            ", nameId=" + nameId +
            '}';
    }
}
