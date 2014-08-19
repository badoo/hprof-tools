package com.badoo.hprof.library.heap;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.ConstantField;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.library.model.StaticField;

import java.io.IOException;
import java.io.OutputStream;

import static com.badoo.hprof.library.util.StreamUtil.write;
import static com.badoo.hprof.library.util.StreamUtil.writeByte;
import static com.badoo.hprof.library.util.StreamUtil.writeInt;
import static com.badoo.hprof.library.util.StreamUtil.writeShort;

/**
 * Created by Erik Andre on 15/08/2014.
 */
public class HeapDumpWriter {

    private final OutputStream out;

    public HeapDumpWriter(OutputStream out) {
        this.out = out;
    }

    /**
     * Write a CLASS_DUMP heap record.
     *
     * @param cls The class to be dumped
     */
    public void writeClassDumpRecord(ClassDefinition cls) throws IOException {
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

}
