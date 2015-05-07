package com.badoo.bmd.model;

/**
 * Class representing an BMD object array. Unlike primitive arrays it retains the element data.
 * <p/>
 * Created by Erik Andre on 09/11/14.
 */
public class BmdObjectArray {

    private final int id;
    private final int elementClassId;
    private final int[] elements;

    public BmdObjectArray(int id, int elementClassId, int[] elements) {
        this.id = id;
        this.elementClassId = elementClassId;
        this.elements = elements;
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
     * Returns the class id of the element objects.
     *
     * @return The element class id
     */
    public int getElementClassId() {
        return elementClassId;
    }

    /**
     * Returns the element data (object ids referencing instances).
     *
     * @return The elements
     */
    public int[] getElements() {
        return elements;
    }
}
