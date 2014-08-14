package com.badoo.hprof.library.model;

/**
 * Created by Erik Andre on 24/06/2014.
 */
public class InstanceField implements NamedField {

    private BasicType type;

    private int nameId;

    private boolean enabled;

    public InstanceField(BasicType type, int nameId) {
        this.type = type;
        this.nameId = nameId;
        enabled = type == BasicType.OBJECT;
    }

    public BasicType getType() {
        return type;
    }

    @Override
    public int getFieldNameId() {
        return nameId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setType(BasicType type) {
        this.type = type;
    }

    @Override
    public void setFieldNameId(int nameId) {
        this.nameId = nameId;
    }
}
