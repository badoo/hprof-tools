package com.badoo.hprof.library.heap.processor;

import com.badoo.hprof.library.heap.HeapDumpProcessor;
import com.badoo.hprof.library.heap.HeapDumpReader;
import com.badoo.hprof.library.heap.HeapTag;
import com.badoo.hprof.library.model.BasicType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Nonnull;

import static com.badoo.hprof.library.util.StreamUtil.ID_SIZE;
import static com.badoo.hprof.library.util.StreamUtil.U1_SIZE;
import static com.badoo.hprof.library.util.StreamUtil.U2_SIZE;
import static com.badoo.hprof.library.util.StreamUtil.U4_SIZE;
import static com.badoo.hprof.library.util.StreamUtil.copy;
import static com.badoo.hprof.library.util.StreamUtil.readInt;
import static com.badoo.hprof.library.util.StreamUtil.readShort;
import static com.badoo.hprof.library.util.StreamUtil.skip;
import static com.badoo.hprof.library.util.StreamUtil.writeInt;
import static com.badoo.hprof.library.util.StreamUtil.writeShort;

/**
 * Base class for processing heap dump records.
 * 
 * Created by Erik Andre on 14/08/2014.
 */
public abstract class HeapDumpBaseProcessor implements HeapDumpProcessor {

    @Override
    public abstract void onHeapRecord(int tag, @Nonnull HeapDumpReader reader) throws IOException;

    protected void skipHeapRecord(int tag, InputStream in) throws IOException {
//        System.out.println("tag: " + Integer.toHexString(tag).toUpperCase());
        switch (tag) {
            case HeapTag.ROOT_UNKNOWN:
                skip(in, ID_SIZE); // Object id
                break;
            case HeapTag.ROOT_JNI_GLOBAL:
                skip(in, 2* ID_SIZE); // Object id + JNI global ref Id
                break;
            case HeapTag.ROOT_JNI_LOCAL:
                skip(in, ID_SIZE+2*U4_SIZE); // Object id + thread serial + frame number
                break;
            case HeapTag.ROOT_JAVA_FRAME:
                skip(in, ID_SIZE + 2* U4_SIZE); // Object id + thread serial + frame number
                break;
            case HeapTag.ROOT_NATIVE_STACK:
                skip(in, ID_SIZE+U4_SIZE); // Object id + thread serial
                break;
            case HeapTag.ROOT_STICKY_CLASS:
                skip(in, ID_SIZE); // Object id
                break;
            case HeapTag.ROOT_THREAD_BLOCK:
                skip(in, ID_SIZE+ U4_SIZE); // Object id + thread serial
                break;
            case HeapTag.ROOT_MONITOR_USED:
                skip(in, ID_SIZE); // Object id
                break;
            case HeapTag.ROOT_THREAD_OBJECT:
                skip(in, ID_SIZE + 2* U4_SIZE); // Object id + thread serial + stack serial
                break;
            case HeapTag.CLASS_DUMP: {

                skip(in, 7 * ID_SIZE + 2 * U4_SIZE); // Object id + stack trace serial + super class object id + class loader object id + signers object id + protection domain id + 2 x reserved + instance size
                short constantCount = readShort(in);
                for (int i = 0; i < constantCount; i++) {
                    skip(in, U2_SIZE); // Pool index
                    BasicType type = BasicType.fromType(in.read());
                    skip(in, type.size);
                }
                short staticCount = readShort(in);
                for (int i = 0; i < staticCount; i++) {
                    skip(in, ID_SIZE); // Name string id
                    BasicType type = BasicType.fromType(in.read());
                    skip(in, type.size);
                }
                short fieldCount = readShort(in);
                for (int i = 0; i < fieldCount; i++) {
                    skip(in, ID_SIZE); // Name string id
                    skip(in, U1_SIZE); // Field type
                }
                break;
            }
            case HeapTag.INSTANCE_DUMP: {
                skip(in, U4_SIZE+ 2* ID_SIZE); // Object id + stack trace serial + class object id
                int size = readInt(in);
                skip(in, size);
                break;
            }
            case HeapTag.OBJECT_ARRAY_DUMP: {
                skip(in, ID_SIZE+U4_SIZE); // Object id + thread serial
                int count = readInt(in);
                skip(in, ID_SIZE); // Array class object id
                skip(in, ID_SIZE * count); // Array elements
                break;
            }
            case HeapTag.PRIMITIVE_ARRAY_DUMP: {
                skip(in, ID_SIZE +U4_SIZE); // Object id + thread serial
                int count = readInt(in);
                BasicType type = BasicType.fromType(in.read());
                skip(in, type.size * count);
                break;
            }
            // Android tags below
            case HeapTag.HPROF_HEAP_DUMP_INFO:
                skip(in, ID_SIZE+4); // Object id + data
                break;
            case HeapTag.HPROF_ROOT_INTERNED_STRING:
                skip(in, ID_SIZE); // Object id
                break;
            case HeapTag.HPROF_ROOT_FINALIZING:
                skip(in, ID_SIZE); // Object id
                break;
            case HeapTag.HPROF_ROOT_DEBUGGER:
                skip(in, ID_SIZE); // Object id
                break;
            case HeapTag.HPROF_ROOT_REFERENCE_CLEANUP:
                skip(in, ID_SIZE); // Object id
                break;
            case HeapTag.HPROF_ROOT_VM_INTERNAL:
                skip(in, ID_SIZE); // Object id
                break;
            case HeapTag.HPROF_ROOT_JNI_MONITOR:
                skip(in, ID_SIZE+ 8); // Object id + data
                break;
            case HeapTag.HPROF_UNREACHABLE:
                skip(in, ID_SIZE); // Object id
                break;
            case HeapTag.HPROF_PRIMITIVE_ARRAY_NODATA_DUMP:
                skip(in, ID_SIZE +9); // Object id + data
                break;
            default:
                System.out.println("Failed! tag: " + Integer.toHexString(tag));
                throw new IllegalArgumentException("Heap tag " + Integer.toHexString(tag) + " not supported!");
        }
    }

    protected void copyHeapRecord(int tag, InputStream in, OutputStream out) throws IOException {
        out.write(tag);
        switch (tag) {
            case HeapTag.ROOT_UNKNOWN:
                copy(in, out, ID_SIZE); // Object id
                break;
            case HeapTag.ROOT_JNI_GLOBAL:
                copy(in, out, 2* ID_SIZE ); // Object id + JNI global ref ID
                break;
            case HeapTag.ROOT_JNI_LOCAL:
                copy(in, out, ID_SIZE + 2 * U4_SIZE); // Object id + thread serial + frame number
                break;
            case HeapTag.ROOT_JAVA_FRAME:
                copy(in, out, ID_SIZE + 2 * U4_SIZE); // Object id + thread serial + frame number
                break;
            case HeapTag.ROOT_NATIVE_STACK:
                copy(in, out, ID_SIZE + U4_SIZE); // Object id + thread serial
                break;
            case HeapTag.ROOT_STICKY_CLASS:
                copy(in, out, ID_SIZE); // Object id
                break;
            case HeapTag.ROOT_THREAD_BLOCK:
                copy(in, out, ID_SIZE + U4_SIZE ); // Object id + thread serial
                break;
            case HeapTag.ROOT_MONITOR_USED:
                copy(in, out, ID_SIZE); // Object id
                break;
            case HeapTag.ROOT_THREAD_OBJECT:
                copy(in, out, ID_SIZE + 2 * U4_SIZE); // Object id + thread serial + stack serial
                break;
            case HeapTag.CLASS_DUMP: {

                copy(in, out, 7 * ID_SIZE + 2 * U4_SIZE); // Object id + stack trace serial + super class object id + class loader object id + signers object id + protection domain id + 2 x reserved + instance size
                short constantCount = readShort(in);
                writeShort(out, constantCount);
                for (int i = 0; i < constantCount; i++) {
                    copy(in, out, U2_SIZE); // Pool index
                    BasicType type = BasicType.fromType(in.read());
                    out.write(type.type);
                    copy(in, out, type.size);
                }
                short staticCount = readShort(in);
                writeShort(out, staticCount);
                for (int i = 0; i < staticCount; i++) {
                    copy(in, out, ID_SIZE); // Name string id
                    BasicType type = BasicType.fromType(in.read());
                    out.write(type.type);
                    copy(in, out, type.size);
                }
                short fieldCount = readShort(in);
                writeShort(out, fieldCount);
                for (int i = 0; i < fieldCount; i++) {
                    copy(in, out, ID_SIZE + 1); // Name string id + type
                }
                break;
            }
            case HeapTag.INSTANCE_DUMP: {
//                copy(in, out, 12); // Object id + stack trace serial + class object id
                copy(in, out, U4_SIZE + 2 * ID_SIZE); // Object id + stack trace serial + class object id
                int size = readInt(in);
                writeInt(out, size);
                copy(in, out, size);
                break;
            }
            case HeapTag.OBJECT_ARRAY_DUMP: {
//                copy(in, out, 8); // Object id + thread serial
                copy(in, out,  ID_SIZE+ U4_SIZE ); // Object id + thread serial
                int count = readInt(in);
                writeInt(out, count);
                copy(in, out, ID_SIZE); // Array class object id
                copy(in, out, ID_SIZE * count); // Array elements
                break;
            }
            case HeapTag.PRIMITIVE_ARRAY_DUMP: {
                copy(in, out, ID_SIZE + U4_SIZE); // Object id + thread serial
                int count = readInt(in);
                writeInt(out, count);
                BasicType type = BasicType.fromType(in.read());
                out.write(type.type);
                copy(in, out, type.size * count);
                break;
            }
            // Android tags below
            case HeapTag.HPROF_HEAP_DUMP_INFO:
                copy(in, out, ID_SIZE + U4_SIZE); // Object id + data
                break;
            case HeapTag.HPROF_ROOT_INTERNED_STRING:
                copy(in, out, ID_SIZE); // Object id
                break;
            case HeapTag.HPROF_ROOT_FINALIZING:
                copy(in, out, ID_SIZE); // Object id
                break;
            case HeapTag.HPROF_ROOT_DEBUGGER:
                copy(in, out, ID_SIZE); // Object id
                break;
            case HeapTag.HPROF_ROOT_REFERENCE_CLEANUP:
                copy(in, out, ID_SIZE); // Object id
                break;
            case HeapTag.HPROF_ROOT_VM_INTERNAL:
                copy(in, out, ID_SIZE); // Object id
                break;
            case HeapTag.HPROF_ROOT_JNI_MONITOR:
                copy(in, out, ID_SIZE + 8); // Object id + data
                break;
            case HeapTag.HPROF_UNREACHABLE:
                copy(in, out, ID_SIZE); // Object id
                break;
            case HeapTag.HPROF_PRIMITIVE_ARRAY_NODATA_DUMP:
                copy(in, out, ID_SIZE+9); // Object id + data
                break;
            default:
                System.out.println("Failed! tag: " + Integer.toHexString(tag));
                throw new IllegalArgumentException("Heap tag " + Integer.toHexString(tag) + " not supported!");
        }
    }
}
