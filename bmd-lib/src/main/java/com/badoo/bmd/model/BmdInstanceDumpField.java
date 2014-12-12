package com.badoo.bmd.model;

/**
 * Class containing the data from a instance field in an instance dump.
 *
 * Created by Erik Andre on 09/11/14.
 */
public class BmdInstanceDumpField extends BmdInstanceFieldDefinition {

    private final Object data;

    public BmdInstanceDumpField(BmdInstanceFieldDefinition fieldDefinition, Object data) {
        super(fieldDefinition.getNameId(), fieldDefinition.getType());
        this.data = data;
    }

    public BmdInstanceDumpField(int nameId, BmdBasicType type, Object data) {
        super(nameId, type);
        this.data = data;
    }
}
