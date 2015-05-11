package com.badoo.hprof.library.heap;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.ConstantField;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.library.model.StaticField;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Nonnull;

import static com.badoo.hprof.library.util.StreamUtil.write;
import static com.badoo.hprof.library.util.StreamUtil.writeByte;
import static com.badoo.hprof.library.util.StreamUtil.writeInt;
import static com.badoo.hprof.library.util.StreamUtil.writeShort;

/**
 * Class containing methods for writing the sub records of a HEAP_DUMP or HEAP_DUMP_SECTION record.
 * <p/>
 * Created by Erik Andre on 15/08/2014.
 */
public class HeapDumpWriter {

    private final OutputStream out;

    public HeapDumpWriter(@Nonnull OutputStream out) {
        this.out = out;
    }

    /**
     * Returns the underlying OutputStream to which the records are written.
     *
     * @return The OutputStream
     */
    @Nonnull
    public OutputStream getOutputStream() {
        return out;
    }

    /**
     * Write a CLASS_DUMP heap record.
     *
     * @param cls The class to be dumped
     */
    public void writeClassDumpRecord(@Nonnull ClassDefinition cls) throws IOException {
        out.write(HeapTag.CLASS_DUMP);
        writeInt(out, cls.getObjectId());
        writeInt(out, cls.getStackTraceSerial());
        writeInt(out, cls.getSuperClassObjectId());
        writeInt(out, cls.getClassLoaderObjectId());
        writeInt(out, cls.getSignersObjectId());
        writeInt(out, cls.getProtectionDomainObjectId());
        writeInt(out, 0); // Reserved
        writeInt(out, 0); // Reserved
        writeInt(out, cls.getInstanceSize());
        // Write constant fields
        writeShort(out, (short) cls.getConstantFields().size());
        for (ConstantField field : cls.getConstantFields()) {
            writeShort(out, field.getPoolIndex());
            writeByte(out, field.getType().type);
            write(out, field.getValue());
        }
        // Write static fields
        writeShort(out, (short) cls.getStaticFields().size());
        for (StaticField field : cls.getStaticFields()) {
            writeInt(out, field.getFieldNameId());
            writeByte(out, field.getType().type);
            write(out, field.getValue());
        }
        // Write instance fields
        writeShort(out, (short) cls.getInstanceFields().size());
        for (InstanceField field : cls.getInstanceFields()) {
            writeInt(out, field.getFieldNameId());
            writeByte(out, field.getType().type);
        }
    }

    /**
     * Write an INSTANCE_DUMP record.
     *
     * @param objectId         Object id of the instance
     * @param stackTraceSerial Stack trace serial number
     * @param classId          Id of the instance's class
     * @param data             Instance data (packed instance field values)
     */
    public void writeInstanceDumpRecord(int objectId, int stackTraceSerial, int classId, @Nonnull byte[] data) throws IOException {
        out.write(HeapTag.INSTANCE_DUMP);
        writeInt(out, objectId);
        writeInt(out, stackTraceSerial);
        writeInt(out, classId);
        writeInt(out, data.length);
        write(out, data);
    }

    /**
     * Write an object array (OBJECT_ARRAY_DUMP) to the heap dump.
     *
     * @param objectId         The array object id
     * @param stackTraceSerial Stack trace serial number
     * @param elementClassId   Class id of the elements
     * @param elements         An array containing the object ids of the elements
     */
    public void writeObjectArray(int objectId, int stackTraceSerial, int elementClassId, @Nonnull int[] elements) throws IOException {
        out.write(HeapTag.OBJECT_ARRAY_DUMP);
        writeInt(out, objectId);
        writeInt(out, stackTraceSerial);
        writeInt(out, elements.length);
        writeInt(out, elementClassId);
        for (int element : elements) {
            writeInt(out, element);
        }
    }

    /**
     * Write the header for a primitive array (PRIMITIVE_ARRAY_DUMP) record. This must be followed by the array contents.
     *
     * @param objectId         The array object id
     * @param stackTraceSerial Stack trace serial number
     * @param elementType      The basic type of the elements in the array
     * @param length           Length of the array
     */
    public void writePrimitiveArrayHeader(int objectId, int stackTraceSerial, @Nonnull BasicType elementType, int length) throws IOException {
        out.write(HeapTag.PRIMITIVE_ARRAY_DUMP);
        writeInt(out, objectId);
        writeInt(out, stackTraceSerial);
        writeInt(out, length);
        writeByte(out, elementType.type);
    }

    /**
     * Write an unknown GC (ROOT_UNKNOWN) root.
     *
     * @param objectId The object id of the root object.
     */
    public void writeUnknownRoot(int objectId) throws IOException {
        out.write(HeapTag.ROOT_UNKNOWN);
        writeInt(out, objectId);
    }
}
