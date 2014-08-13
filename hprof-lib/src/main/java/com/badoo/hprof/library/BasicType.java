package com.badoo.hprof.library;

/**
 * Created by Erik Andre on 21/06/2014.
 */
public enum BasicType {

    OBJECT(2, 4),
    BOOLEAN(4, 1),
    CHAR(5, 2),
    FLOAT(6, 4),
    DOUBLE(7, 8),
    BYTE(8, 1),
    SHORT(9, 2),
    INT(10, 4),
    LONG(11, 8);

    public final int type;
    public final int size;

    BasicType(int type, int size) {
        this.type = type;
        this.size = size;
    }

    public static BasicType fromType(int type) {
        switch (type) {
            case 2:
                return OBJECT;
            case 4:
                return BOOLEAN;
            case 5:
                return CHAR;
            case 6:
                return FLOAT;
            case 7:
                return DOUBLE;
            case 8:
                return BYTE;
            case 9:
                return SHORT;
            case 10:
                return INT;
            case 11:
                return LONG;
            default:
                throw new IllegalArgumentException("BasicType " + type + " not supported");
        }
    }
}
