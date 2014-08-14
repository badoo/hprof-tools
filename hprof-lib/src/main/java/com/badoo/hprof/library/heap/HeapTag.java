package com.badoo.hprof.library.heap;

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

}
