package com.badoo.bmd;

import javax.annotation.Nonnull;

/**
 * Enum listing of all valid tags (an id that specifies the type of a record).
 * <p/>
 * Created by Erik Andre on 22/10/14.
 */
public enum BmdTag {

    STRING(1),
    HASHED_STRING(2),
    CLASS_DEFINITION(3),
    INSTANCE_DUMP (4),
    ROOT_OBJECTS(5),
    OBJECT_ARRAY(6),
    PRIMITIVE_ARRAY_PLACEHOLDER(7),
    LEGACY_HPROF_RECORD(8);

    public static BmdTag fromValue(int value) {
        switch (value) {
            case 1:
                return STRING;
            case 2:
                return HASHED_STRING;
            case 3:
                return CLASS_DEFINITION;
            case 4:
                return INSTANCE_DUMP;
            case 5:
                return ROOT_OBJECTS;
            case 6:
                return OBJECT_ARRAY;
            case 7:
                return PRIMITIVE_ARRAY_PLACEHOLDER;
            case 8:
                return LEGACY_HPROF_RECORD;
            default:
                throw new IllegalArgumentException("No BmdTag with value " + value);
        }
    }

    public final int value;


    BmdTag(int value) {
        this.value = value;
    }

}
