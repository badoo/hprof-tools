package com.badoo.bmd.model;

/**
 * Enum defining the data types supported by BMD.
 * <p/>
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

    public static BmdBasicType fromInt(int id) {
        switch (id) {
            case 0:
                return OBJECT;
            case 1:
                return INT;
            case 2:
                return BOOLEAN;
            case 3:
                return BYTE;
            case 4:
                return CHAR;
            case 5:
                return FLOAT;
            case 6:
                return DOUBLE;
            case 7:
                return LONG;
            case 8:
                return SHORT;
            default:
                throw new IllegalArgumentException("Invalid value for BmdBasicType");
        }
    }
}
