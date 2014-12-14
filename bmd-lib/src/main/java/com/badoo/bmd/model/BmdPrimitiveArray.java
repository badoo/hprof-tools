package com.badoo.bmd.model;

/**
 * Class representing an BMD primitive array. Does not contain any element data, only type and the original size.
 * <p/>
 * Created by Erik Andre on 09/11/14.
 */
public class BmdPrimitiveArray {

    private final int id;
    private final BmdBasicType type;
    private final int elementCount;

    public BmdPrimitiveArray(int id, BmdBasicType type, int elementCount) {
        this.id = id;
        this.type = type;
        this.elementCount = elementCount;
    }

    /**
     * Returns the object id of the array.
     *
     * @return The object id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the type of the elements.
     *
     * @return The element type
     */
    public BmdBasicType getType() {
        return type;
    }

    /**
     * Returns the count of elements.
     *
     * @return The element count
     */
    public int getElementCount() {
        return elementCount;
    }
}
