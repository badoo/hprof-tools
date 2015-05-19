package com.badoo.hprof.library;

import javax.annotation.Nonnull;

/**
 * Tags (types) of hprof records. The tag parameter in HprofProcessor.onRecord() will be one of these values.
 * <p/>
 * Source: https://java.net/downloads/heap-snapshot/hprof-binary-format.html
 * <p/>
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

    private Tag() {
    }

    /**
     * Convert an integer HPROF tag value to a readable string representation (used for debugging).
     *
     * @param tag the tag to convert
     */
    @Nonnull
    public static String tagToString(int tag) {
        switch (tag) {
            case UNKNOWN:
                return "UNKNOWN";
            case STRING:
                return "STRING";
            case LOAD_CLASS:
                return "LOAD_CLASS";
            case UNLOAD_CLASS:
                return "UNLOAD_CLASS";
            case STACK_FRAME:
                return "STACK_FRAME";
            case STACK_TRACE:
                return "STACK_TRACE";
            case ALLOC_SITES:
                return "ALLOC_SITES";
            case HEAP_SUMMARY:
                return "HEAP_SUMMARY";
            case START_THREAD:
                return "START_THREAD";
            case END_THREAD:
                return "END_THREAD";
            case HEAP_DUMP:
                return "HEAP_DUMP";
            case HEAP_DUMP_SEGMENT:
                return "HEAP_DUMP_SEGMENT";
            case HEAP_DUMP_END:
                return "HEAP_DUMP_END";
            case CPU_SAMPLES:
                return "CPU_SAMPLES";
            case CONTROL_SETTINGS:
                return "CONTROL_SETTINGS";
            default:
                return "Unknown tag";
        }
    }
}
