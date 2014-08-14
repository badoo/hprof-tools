package com.badoo.hprof.library.heap.processor;

import com.badoo.hprof.library.heap.HeapDumpProcessor;
import com.badoo.hprof.library.heap.HeapTag;
import com.badoo.hprof.library.model.BasicType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.badoo.hprof.library.IoUtil.copy;
import static com.badoo.hprof.library.IoUtil.readInt;
import static com.badoo.hprof.library.IoUtil.readShort;
import static com.badoo.hprof.library.IoUtil.writeInt;
import static com.badoo.hprof.library.IoUtil.writeShort;

/**
 * A HeapDumpProcessor that reads each record and discards the data
 * <p/>
 * Created by Erik Andre on 14/08/2014.
 */
public class HeapDumpCopyProcessor implements HeapDumpProcessor {

    private final OutputStream out;

    public HeapDumpCopyProcessor(OutputStream out) {
        this.out = out;
    }

    @Override
    public void onHeapRecord(int tag, InputStream in) throws IOException {
        out.write(tag);
        switch (tag) {
            case HeapTag.ROOT_UNKNOWN:
                copy(in, out, 4); // Object id
                break;
            case HeapTag.ROOT_JNI_GLOBAL:
                copy(in, out, 8); // Object id + JNI global ref
                break;
            case HeapTag.ROOT_JNI_LOCAL:
                copy(in, out, 12); // Object id + thread serial + frame number
                break;
            case HeapTag.ROOT_JAVA_FRAME:
                copy(in, out, 12); // Object id + thread serial + frame number
                break;
            case HeapTag.ROOT_NATIVE_STACK:
                copy(in, out, 8); // Object id + thread serial
                break;
            case HeapTag.ROOT_STICKY_CLASS:
                copy(in, out, 4); // Object id
                break;
            case HeapTag.ROOT_THREAD_BLOCK:
                copy(in, out, 8); // Object id + thread serial
                break;
            case HeapTag.ROOT_MONITOR_USED:
                copy(in, out, 4); // Object id
                break;
            case HeapTag.ROOT_THREAD_OBJECT:
                copy(in, out, 12); // Object id + thread serial + stack serial
                break;
            case HeapTag.CLASS_DUMP: {
                copy(in, out, 36); // Object id + stack trace serial + super class object id + class loader object id + signers object id + protection domain id + 2 x reserved + instance size
                short constantCount = readShort(in);
                writeShort(out, constantCount);
                for (int i = 0; i < constantCount; i++) {
                    copy(in, out, 2); // Pool index
                    BasicType type = BasicType.fromType(in.read());
                    out.write(type.type);
                    copy(in, out, type.size);
                }
                short staticCount = readShort(in);
                writeShort(out, staticCount);
                for (int i = 0; i < staticCount; i++) {
                    copy(in, out, 4); // Name string id
                    BasicType type = BasicType.fromType(in.read());
                    out.write(type.type);
                    copy(in, out, type.size);
                }
                short fieldCount = readShort(in);
                writeShort(out, fieldCount);
                for (int i = 0; i < fieldCount; i++) {
                    copy(in, out, 5); // Name string id + type
                }
                break;
            }
            case HeapTag.INSTANCE_DUMP: {
                copy(in, out, 12); // Object id + stack trace serial + class object id
                int size = readInt(in);
                writeInt(out, size);
                copy(in, out, size);
                break;
            }
            case HeapTag.OBJECT_ARRAY_DUMP: {
                copy(in, out, 8); // Object id + thread serial
                int count = readInt(in);
                writeInt(out, count);
                copy(in, out, 4); // Array class object id
                copy(in, out, 4 * count); // Array elements
                break;
            }
            case HeapTag.PRIMITIVE_ARRAY_DUMP: {
                copy(in, out, 8); // Object id + thread serial
                int count = readInt(in);
                writeInt(out, count);
                BasicType type = BasicType.fromType(in.read());
                out.write(type.type);
                copy(in, out, type.size * count);
                break;
            }
            // Android tags below
            case HeapTag.HPROF_HEAP_DUMP_INFO:
                copy(in, out, 8); // Object id + data
                break;
            case HeapTag.HPROF_ROOT_INTERNED_STRING:
                copy(in, out, 4); // Object id
                break;
            case HeapTag.HPROF_ROOT_FINALIZING:
                copy(in, out, 4); // Object id
                break;
            case HeapTag.HPROF_ROOT_DEBUGGER:
                copy(in, out, 4); // Object id
                break;
            case HeapTag.HPROF_ROOT_REFERENCE_CLEANUP:
                copy(in, out, 4); // Object id
                break;
            case HeapTag.HPROF_ROOT_VM_INTERNAL:
                copy(in, out, 4); // Object id
                break;
            case HeapTag.HPROF_ROOT_JNI_MONITOR:
                copy(in, out, 12); // Object id + data
                break;
            case HeapTag.HPROF_UNREACHABLE:
                copy(in, out, 4); // Object id
                break;
            case HeapTag.HPROF_PRIMITIVE_ARRAY_NODATA_DUMP:
                copy(in, out, 13); // Object id + data
                break;
            default:
                System.out.println("Failed! tag: " + Integer.toHexString(tag));
                throw new IllegalArgumentException("Heap tag " + Integer.toHexString(tag) + " not supported!");
        }
    }
}
