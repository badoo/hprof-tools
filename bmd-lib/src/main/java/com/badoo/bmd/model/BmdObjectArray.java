package com.badoo.bmd.model;

/**
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

    public int getId() {
        return id;
    }

    public int getElementClassId() {
        return elementClassId;
    }

    public int[] getElements() {
        return elements;
    }
}
