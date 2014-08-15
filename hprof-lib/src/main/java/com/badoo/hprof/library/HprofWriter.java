package com.badoo.hprof.library;

import com.badoo.hprof.library.heap.HeapTag;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.ConstantField;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.library.model.StaticField;

import java.io.IOException;
import java.io.OutputStream;

import static com.badoo.hprof.library.StreamUtil.write;
import static com.badoo.hprof.library.StreamUtil.writeByte;
import static com.badoo.hprof.library.StreamUtil.writeInt;
import static com.badoo.hprof.library.StreamUtil.writeNullTerminatedString;
import static com.badoo.hprof.library.StreamUtil.writeShort;

/**
 * Class containing methods for writing hprof files
 * <p/>
 * Created by Erik Andre on 13/07/2014.
 */
public class HprofWriter {

    private final OutputStream out;

    public HprofWriter(OutputStream out) {
        this.out = out;
    }

    /**
     * Write the header that is present in the beginning of all hprof files.
     *
     * @param text     A magic text identifying the version and type of hprof file
     * @param idSize   Size in bytes for all ID fields
     * @param timeHigh High four bytes of the file timestamp, all other 2-byte timestamps in the file are relative to this
     * @param timeLow  Low four bytes of the file timestamp
     * @throws IOException
     */
    public void writeHprofFileHeader(String text, int idSize, int timeHigh, int timeLow) throws IOException {
        writeNullTerminatedString(out, text);
        writeInt(out, idSize);
        writeInt(out, timeHigh);
        writeInt(out, timeLow);
    }

    /**
     * Write a record header
     *
     * @param tag       The tag, see definitions in Tag
     * @param timestamp Timestamp for the record
     * @param length    Length in bytes of the record (not including the header)
     * @throws IOException
     */
    public void writeRecordHeader(int tag, int timestamp, int length) throws IOException {
        out.write(tag);
        writeInt(out, timestamp);
        writeInt(out, length);
    }

    /**
     * Write a string record (including the header).
     *
     * @param id        The string id
     * @param timestamp Timestamp for the record
     * @param string    The string
     */
    public void writeStringRecord(int id, int timestamp, String string) throws IOException {
        byte[] stringData = string.getBytes();
        writeRecordHeader(Tag.STRING, timestamp, stringData.length + 4);
        writeInt(out, id);
        write(out, stringData);
    }

    // Methods for writing heap records

    /**
     * Write a CLASS_DUMP heap record. This must be contained within a HEAP_DUMP or HEAP_DUMP_SEGMENT record.
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
