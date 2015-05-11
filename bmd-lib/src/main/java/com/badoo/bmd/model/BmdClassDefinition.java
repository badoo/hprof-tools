package com.badoo.bmd.model;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Data class for BMD class definitions.
 * <p/>
 * Created by Erik Andre on 23/10/14.
 */
public class BmdClassDefinition {

    private final int id;
    private final int superClassId;
    private final int name;
    private final List<BmdConstantField> constantFields;
    private final List<BmdStaticField> staticFields;
    private final List<BmdInstanceFieldDefinition> instanceFields;
    private final int discardedFieldSize;

    public BmdClassDefinition(int id, int superClassId, int name, @Nonnull List<BmdConstantField> constantFields, @Nonnull List<BmdStaticField> staticFields, @Nonnull List<BmdInstanceFieldDefinition> instanceFields, int discardedFieldSize) {
        this.id = id;
        this.superClassId = superClassId;
        this.name = name;
        this.constantFields = constantFields;
        this.staticFields = staticFields;
        this.instanceFields = instanceFields;
        this.discardedFieldSize = discardedFieldSize;
    }

    /**
     * Returns the class id.
     *
     * @return The class id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the super class id.
     *
     * @return The super class id
     */
    public int getSuperClassId() {
        return superClassId;
    }

    /**
     * Returns the string id of the class' name.
     *
     * @return The string id of the name
     */
    public int getName() {
        return name;
    }

    /**
     * Returns a list of the constant fields of the class.
     *
     * @return The class' constant fields
     */
    @Nonnull
    public List<BmdConstantField> getConstantFields() {
        return constantFields;
    }

    /**
     * Returns a list of static fields of the class.
     *
     * @return The class' static fields
     */
    @Nonnull
    public List<BmdStaticField> getStaticFields() {
        return staticFields;
    }

    /**
     * Returns a list of instance fields of the class.
     *
     * @return The class' instance fields
     */
    @Nonnull
    public List<BmdInstanceFieldDefinition> getInstanceFields() {
        return instanceFields;
    }

    /**
     * Returns the size in bytes of fields that were removed due to size optimization.
     *
     * @return The size in bytes or discarded fields
     */
    public int getDiscardedFieldSize() {
        return discardedFieldSize;
    }
}
