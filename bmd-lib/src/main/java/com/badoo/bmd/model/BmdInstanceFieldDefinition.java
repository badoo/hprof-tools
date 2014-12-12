package com.badoo.bmd.model;

/**
 * Data class for instance fields in BMD class definitions.
 * <p/>
 * Created by Erik Andre on 02/11/14.
 */
public class BmdInstanceFieldDefinition {

    private final int nameId;
    private final BmdBasicType type;

    public BmdInstanceFieldDefinition(int nameId, BmdBasicType type) {
        this.nameId = nameId;
        this.type = type;
    }

    public int getNameId() {
        return nameId;
    }

    public BmdBasicType getType() {
        return type;
    }
}
