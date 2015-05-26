package com.badoo.bmd.model;

import javax.annotation.Nonnull;

/**
 * Class containing the data from a instance field in an instance dump.
 * <p/>
 * Created by Erik Andre on 09/11/14.
 */
public class BmdInstanceDumpField extends BmdInstanceFieldDefinition {

    private final Object data;
    private final BmdClassDefinition classDefinition;

    /**
     * Creates a new instance dump field based on an existing class definition.
     *
     * @param classDefinition The class that this field belongs to
     * @param fieldDefinition The existing field definition
     * @param data            The actual field data (can be a boxed primitive or an Integer representing an object reference)
     */
    public BmdInstanceDumpField(@Nonnull BmdClassDefinition classDefinition, @Nonnull BmdInstanceFieldDefinition fieldDefinition, @Nonnull Object data) {
        super(fieldDefinition.getNameId(), fieldDefinition.getType());
        this.data = data;
        this.classDefinition = classDefinition;
    }

    /**
     * @param classDefinition The class that this field belongs to
     * @param nameId          Name of the field (string id)
     * @param type            Type of the field
     * @param data            The actual field data (can be a boxed primitive or an Integer representing an object reference)
     */
    @SuppressWarnings("UnusedDeclaration")
    public BmdInstanceDumpField(@Nonnull BmdClassDefinition classDefinition, int nameId, @Nonnull BmdBasicType type, Object data) {
        super(nameId, type);
        this.data = data;
        this.classDefinition = classDefinition;
    }

    /**
     * Returns the class definition of the class that this fields belongs to.
     *
     * @return The class definition
     */
    @Nonnull
    public BmdClassDefinition getClassDefinition() {
        return classDefinition;
    }

    /**
     * Returns the value of the field.
     *
     * @return The field value
     */
    @Nonnull
    public Object getData() {
        return data;
    }
}
