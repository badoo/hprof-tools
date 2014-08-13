package com.badoo.hprof.library;

/**
 * Created by Erik Andre on 20/06/2014.
 */
public final class Tag {

    public static final int UNKNOWN = 0;
    public static final int STRING = 0x1;
    public static final int LOAD_CLASS = 0x2;
    public static final int UNLOAD_CLASS = 0x3;
    public static final int STACK_FRAME = 0x4;
    public static final int STACK_TRACE = 0x5;
    public static final int ALLOC_SITES = 0x6;
    public static final int HEAP_SUMMARY = 0x7;
    public static final int START_THREAD = 0xa;
    public static final int END_THREAD = 0xb;
    public static final int HEAP_DUMP = 0xc;
    public static final int HEAP_DUMP_SEGMENT = 0x1c;
    public static final int HEAP_DUMP_END = 0x2c;
    public static final int CPU_SAMPLES = 0xd;
    public static final int CONTROL_SETTINGS = 0xe;


    public final int value;

    Tag(int value) {
        this.value = value;
    }

    public static Tag fromValue(int value) {
        switch (value) {
            case 0x1:
                return STRING;
            case 0x2:
                return LOAD_CLASS;
            case 0x3:
                return UNLOAD_CLASS;
            case 0x4:
                return STACK_FRAME;
            case 0x5:
                return STACK_TRACE;
            case 0x6:
                return ALLOC_SITES;
            case 0x7:
                return HEAP_SUMMARY;
            case 0xa:
                return START_THREAD;
            case 0xb:
                return END_THREAD;
            case 0xc:
                return HEAP_DUMP;
            case 0x1c:
                return HEAP_DUMP_SEGMENT;
            case 0x2c:
                return HEAP_DUMP_END;
            case 0xd:
                return CPU_SAMPLES;
            case 0xe:
                return CONTROL_SETTINGS;
            case 0x30:
                return STRING_HASH;
            default:
                return UNKNOWN;
        }
    }
}
