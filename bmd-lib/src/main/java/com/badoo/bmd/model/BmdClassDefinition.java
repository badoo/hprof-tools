package com.badoo.bmd.model;

import java.util.List;

/**
 * Data class for BMD class definitions.
 * <p/>
 * Created by Erik Andre on 23/10/14.
 */
@SuppressWarnings("UnusedDeclaration")
public class BmdClassDefinition {

    private final int id;
    private final int superClassId;
    private final int name;
    private final List<BmdConstantField> constantFields;
    private final List<BmdStaticField> staticFields;
    private final List<BmdInstanceFieldDefinition> instanceFields;
    private final int discardedFieldSize;

    public BmdClassDefinition(int id, int superClassId, int name, List<BmdConstantField> constantFields, List<BmdStaticField> staticFields, List<BmdInstanceFieldDefinition> instanceFields, int discardedFieldSize) {
        this.id = id;
        this.superClassId = superClassId;
        this.name = name;
        this.constantFields = constantFields;
        this.staticFields = staticFields;
        this.instanceFields = instanceFields;
        this.discardedFieldSize = discardedFieldSize;
    }

    public int getId() {
        return id;
    }

    public int getSuperClassId() {
        return superClassId;
    }

    public int getName() {
        return name;
    }

    public List<BmdConstantField> getConstantFields() {
        return constantFields;
    }

    public List<BmdStaticField> getStaticFields() {
        return staticFields;
    }

    public List<BmdInstanceFieldDefinition> getInstanceFields() {
        return instanceFields;
    }

    public int getDiscardedFieldSize() {
        return discardedFieldSize;
    }
}
