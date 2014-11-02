package com.badoo.bmd.model;

/**
 * Created by Erik Andre on 23/10/14.
 */
public enum BmdBasicType {

    OBJECT(0),
    INT(1),
    BOOLEAN(2),
    BYTE(3),
    CHAR(4),
    FLOAT(5),
    DOUBLE(6),
    LONG(7),
    SHORT(8);

    public final int id;

    BmdBasicType(int id) {
        this.id = id;
    }
}
