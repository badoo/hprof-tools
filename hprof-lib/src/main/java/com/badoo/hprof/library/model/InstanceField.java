package com.badoo.hprof.library.model;

/**
 * Class containing the definition of an instance field (part of CLASS_DUMP) but not the actual field value itself (part of INSTANCE_DUMP).
 * <p/>
 * Created by Erik Andre on 24/06/2014.
 */
public class InstanceField implements NamedField {

    private BasicType type;
    private int nameId;

    public InstanceField(BasicType type, int nameId) {
        this.type = type;
        this.nameId = nameId;
    }

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
}
