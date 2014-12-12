package com.badoo.bmd;

/**
 * Created by Erik Andre on 22/10/14.
 */
public class BmdTag {

    private BmdTag() {}

    public static final int STRING = 1;
    public static final int HASHED_STRING = 2;
    public static final int CLASS_DEFINITION = 3;
    public static final int INSTANCE_DUMP = 4;
    public static final int ROOT_OBJECTS = 5;
    public static final int OBJECT_ARRAY = 6;
    public static final int PRIMITIVE_ARRAY_PLACEHOLDER = 7;
    public static final int LEGACY_HPROF_RECORD = 8;

    public static String tagToString(int tag) {
        switch (tag) {
            case STRING:
                return "STRING";
            case HASHED_STRING:
                return "HASHED_STRING";
            case CLASS_DEFINITION:
                return "CLASS_DEFINITION";
            case INSTANCE_DUMP:
                return "INSTANCE_DUMP";
            case ROOT_OBJECTS:
                return "ROOT_OBJECTS";
            case OBJECT_ARRAY:
                return "OBJECT_ARRAY";
            case PRIMITIVE_ARRAY_PLACEHOLDER:
                return "PRIMITIVE_ARRAY_PLACEHOLDER";
            case LEGACY_HPROF_RECORD:
                return "LEGACY_HPROF_RECORD";
            default:
                return "Undefined value (" + tag + ")";
        }
    }

}
