package com.badoo.bmd.model;

import javax.annotation.Nonnull;

/**
 * Data class for instance fields in BMD class definitions.
 * <p/>
 * Created by Erik Andre on 02/11/14.
 */
public class BmdInstanceFieldDefinition {

    private final int nameId;
    private final BmdBasicType type;

    public BmdInstanceFieldDefinition(int nameId, @Nonnull BmdBasicType type) {
        this.nameId = nameId;
        this.type = type;
    }

    /**
     * Returns the string id of this fields name.
     *
     * @return The string id name of the field
     */
    public int getNameId() {
        return nameId;
    }

    /**
     * Returns the type of the field.
     *
     * @return The type of the field
     */
    @Nonnull
    public BmdBasicType getType() {
        return type;
    }
}
