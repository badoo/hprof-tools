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
    public static final int ROOT_OBJECT = 5;
    public static final int OBJECT_ARRAY = 6;
    public static final int PRIMITIVE_ARRAY_PLACEHOLDER = 7;
    public static final int LEGACY_HPROF_RECORD = 8;

}
