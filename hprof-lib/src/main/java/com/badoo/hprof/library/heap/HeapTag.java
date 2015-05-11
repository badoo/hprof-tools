package com.badoo.hprof.library.heap;

import javax.annotation.Nonnull;

/**
 * Created by Erik Andre on 14/08/2014.
 */
public final class HeapTag {

    public static final int ROOT_UNKNOWN = 0xff;

    public static final int ROOT_JNI_GLOBAL = 0x1;
    public static final int ROOT_JNI_LOCAL = 0x2;
    public static final int ROOT_JAVA_FRAME = 0x3;
    public static final int ROOT_NATIVE_STACK = 0x4;
    public static final int ROOT_STICKY_CLASS = 0x5;
    public static final int ROOT_THREAD_BLOCK = 0x6;
    public static final int ROOT_MONITOR_USED = 0x7;
    public static final int ROOT_THREAD_OBJECT = 0x8;
    public static final int CLASS_DUMP = 0x20;
    public static final int INSTANCE_DUMP = 0x21;
    public static final int OBJECT_ARRAY_DUMP = 0x22;
    public static final int PRIMITIVE_ARRAY_DUMP = 0x23;
    /* Android tags */
    public static final int HPROF_HEAP_DUMP_INFO = 0xfe;
    public static final int HPROF_ROOT_INTERNED_STRING = 0x89;
    public static final int HPROF_ROOT_FINALIZING = 0x8a;
    public static final int HPROF_ROOT_DEBUGGER = 0x8b;
    public static final int HPROF_ROOT_REFERENCE_CLEANUP = 0x8c;
    public static final int HPROF_ROOT_VM_INTERNAL = 0x8d;
    public static final int HPROF_ROOT_JNI_MONITOR = 0x8e;
    public static final int HPROF_UNREACHABLE = 0x90;  /* deprecated */
    public static final int HPROF_PRIMITIVE_ARRAY_NODATA_DUMP = 0xc3;

    private HeapTag() {
    }

    @Nonnull
    public static String tagToString(int tag) {
        switch (tag) {
            case ROOT_UNKNOWN:
                return "ROOT_UNKNOWN";
            case ROOT_JNI_GLOBAL:
                return "ROOT_JNI_GLOBAL";
            case ROOT_JNI_LOCAL:
                return "ROOT_JNI_LOCAL";
            case ROOT_JAVA_FRAME:
                return "ROOT_JAVA_FRAME";
            case ROOT_NATIVE_STACK:
                return "ROOT_NATIVE_STACK";
            case ROOT_STICKY_CLASS:
                return "ROOT_STICKY_CLASS";
            case ROOT_THREAD_BLOCK:
                return "ROOT_THREAD_BLOCK";
            case ROOT_MONITOR_USED:
                return "ROOT_MONITOR_USED";
            case ROOT_THREAD_OBJECT:
                return "ROOT_THREAD_OBJECT";
            case CLASS_DUMP:
                return "CLASS_DUMP";
            case INSTANCE_DUMP:
                return "INSTANCE_DUMP";
            case OBJECT_ARRAY_DUMP:
                return "OBJECT_ARRAY_DUMP";
            case PRIMITIVE_ARRAY_DUMP:
                return "PRIMITIVE_ARRAY_DUMP";
            case HPROF_HEAP_DUMP_INFO:
                return "HPROF_HEAP_DUMP_INFO";
            case HPROF_ROOT_INTERNED_STRING:
                return "HPROF_ROOT_INTERNED_STRING";
            case HPROF_ROOT_FINALIZING:
                return "HPROF_ROOT_FINALIZING";
            case HPROF_ROOT_DEBUGGER:
                return "HPROF_ROOT_DEBUGGER";
            case HPROF_ROOT_REFERENCE_CLEANUP:
                return "HPROF_ROOT_REFERENCE_CLEANUP";
            case HPROF_ROOT_VM_INTERNAL:
                return "HPROF_ROOT_VM_INTERNAL";
            case HPROF_ROOT_JNI_MONITOR:
                return "HPROF_ROOT_JNI_MONITOR";
            case HPROF_UNREACHABLE:
                return "HPROF_UNREACHABLE";
            case HPROF_PRIMITIVE_ARRAY_NODATA_DUMP:
                return "HPROF_PRIMITIVE_ARRAY_NODATA_DUMP";
            default:
                    return "Unknown tag " + tag;
        }
    }

}
