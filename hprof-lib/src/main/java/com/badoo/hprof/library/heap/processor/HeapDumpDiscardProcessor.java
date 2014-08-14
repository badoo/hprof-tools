package com.badoo.hprof.library.heap.processor;

import com.badoo.hprof.library.heap.HeapDumpProcessor;
import com.badoo.hprof.library.heap.HeapTag;
import com.badoo.hprof.library.model.BasicType;

import java.io.IOException;
import java.io.InputStream;

import static com.badoo.hprof.library.IoUtil.readInt;
import static com.badoo.hprof.library.IoUtil.readShort;

/**
 * A HeapDumpProcessor that reads each record and discards the data
 *
 * Created by Erik Andre on 14/08/2014.
 */
public class HeapDumpDiscardProcessor implements HeapDumpProcessor {

    @Override
    public void onHeapRecord(int tag, InputStream in) throws IOException {
        switch (tag) {
            case HeapTag.ROOT_UNKNOWN:
                in.skip(4); // Object id
                break;
            case HeapTag.ROOT_JNI_GLOBAL:
                in.skip(8); // Object id + JNI global ref
                break;
            case HeapTag.ROOT_JNI_LOCAL:
                in.skip(12); // Object id + thread serial + frame number
                break;
            case HeapTag.ROOT_JAVA_FRAME:
                in.skip(12); // Object id + thread serial + frame number
                break;
            case HeapTag.ROOT_NATIVE_STACK:
                in.skip(8); // Object id + thread serial
                break;
            case HeapTag.ROOT_STICKY_CLASS:
                in.skip(4); // Object id
                break;
            case HeapTag.ROOT_THREAD_BLOCK:
                in.skip(8); // Object id + thread serial
                break;
            case HeapTag.ROOT_MONITOR_USED:
                in.skip(4); // Object id
                break;
            case HeapTag.ROOT_THREAD_OBJECT:
                in.skip(12); // Object id + thread serial + stack serial
                break;
            case HeapTag.CLASS_DUMP: {
                in.skip(36); // Object id + stack trace serial + super class object id + class loader object id + signers object id + protection domain id + 2 x reserved + instance size
                short constantCount = readShort(in);
                for (int i = 0; i < constantCount; i++) {
                    in.skip(2); // Pool index
                    BasicType type = BasicType.fromType(in.read());
                    in.skip(type.size);
                }
                short staticCount = readShort(in);
                for (int i = 0; i < staticCount; i++) {
                    in.skip(4); // Name string id
                    BasicType type = BasicType.fromType(in.read());
                    in.skip(type.size);
                }
                short fieldCount = readShort(in);
                for (int i = 0; i < fieldCount; i++) {
                    in.skip(4); // Name string id
                    in.skip(1); // Field type
                }
                break;
            }
            case HeapTag.INSTANCE_DUMP: {
                in.skip(12); // Object id + stack trace serial + class object id
                int size = readInt(in);
                in.skip(size);
                break;
            }
            case HeapTag.OBJECT_ARRAY_DUMP: {
                in.skip(8); // Object id + thread serial
                int count = readInt(in);
                in.skip(4); // Array class object id
                in.skip(4 * count); // Array elements
                break;
            }
            case HeapTag.PRIMITIVE_ARRAY_DUMP: {
                in.skip(8); // Object id + thread serial
                int count = readInt(in);
                BasicType type = BasicType.fromType(in.read());
                in.skip(type.size * count);
                break;
            }
            // Android tags below
            case HeapTag.HPROF_HEAP_DUMP_INFO:
                in.skip(8); // Object id + data
                break;
            case HeapTag.HPROF_ROOT_INTERNED_STRING:
                in.skip(4); // Object id
                break;
            case HeapTag.HPROF_ROOT_FINALIZING:
                in.skip(4); // Object id
                break;
            case HeapTag.HPROF_ROOT_DEBUGGER:
                in.skip(4); // Object id
                break;
            case HeapTag.HPROF_ROOT_REFERENCE_CLEANUP:
                in.skip(4); // Object id
                break;
            case HeapTag.HPROF_ROOT_VM_INTERNAL:
                in.skip(4); // Object id
                break;
            case HeapTag.HPROF_ROOT_JNI_MONITOR:
                in.skip(12); // Object id + data
                break;
            case HeapTag.HPROF_UNREACHABLE:
                in.skip(4); // Object id
                break;
            case HeapTag.HPROF_PRIMITIVE_ARRAY_NODATA_DUMP:
                in.skip(13); // Object id + data
                break;
            default:
                System.out.println("Failed! tag: " + Integer.toHexString(tag));
                throw new IllegalArgumentException("Heap tag " + Integer.toHexString(tag) + " not supported!");
        }
    }
}
