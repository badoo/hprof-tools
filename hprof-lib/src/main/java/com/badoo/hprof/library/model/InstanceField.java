package com.badoo.hprof.library.model;

/**
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

    @Override
    public int getFieldNameId() {
        return nameId;
    }

    public void setType(BasicType type) {
        this.type = type;
    }

    @Override
    public void setFieldNameId(int nameId) {
        this.nameId = nameId;
    }
}
