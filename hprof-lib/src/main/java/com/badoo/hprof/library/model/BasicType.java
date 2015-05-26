package com.badoo.hprof.library.model;

import javax.annotation.Nonnull;

/**
 * Definitions of the basic types supported by Java.
 * <p/>
 * Created by Erik Andre on 21/06/2014.
 */
public enum BasicType {

    /**
     * Tag identifying an Object reference.
     */
    OBJECT(2, 4),
    /**
     * Tag identifying a boolean primitive.
     */
    BOOLEAN(4, 1),
    /**
     * Tag identifying a char primitive.
     */
    CHAR(5, 2),
    /**
     * Tag identifying a float primitive.
      */
    FLOAT(6, 4),
    /**
     * Tag identifying a double primitive.
     */
    DOUBLE(7, 8),
    /**
     * Tag identifying a byte primitive.
     */
    BYTE(8, 1),
    /**
     * Tag identifying a short primitive.
     */
    SHORT(9, 2),
    /**
     * Tag identifying a int primitive.
     */
    INT(10, 4),
    /**
     * Tag identifying a long primitive.
     */
    LONG(11, 8);

    /**
     * Type identifier (byte)
     */
    public final int type;

    /**
     * Size in bytes for a field of this type
     */
    public final int size;

    BasicType(int type, int size) {
        this.type = type;
        this.size = size;
    }

    /**
     * Get the BasicType based on its type value (as read from the hprof file)
     *
     * @param type The type value
     * @return A BasicType matching the type value
     */
    @Nonnull
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
