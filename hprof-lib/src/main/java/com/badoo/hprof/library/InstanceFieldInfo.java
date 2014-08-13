package com.badoo.hprof.library;

/**
 * Created by Erik Andre on 24/06/2014.
 */
public class InstanceFieldInfo {

    private final BasicType type;

    private final int nameId;

    private boolean enabled;

    public InstanceFieldInfo(BasicType type, int nameId) {
        this.type = type;
        this.nameId = nameId;
        enabled = type == BasicType.OBJECT;
    }

    public BasicType getType() {
        return type;
    }

    public int getNameId() {
        return nameId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
