package com.badoo.hprof.library;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.Nonnull;

import static com.badoo.hprof.library.util.StreamUtil.write;
import static com.badoo.hprof.library.util.StreamUtil.writeInt;
import static com.badoo.hprof.library.util.StreamUtil.writeNullTerminatedString;

/**
 * Class containing methods for writing hprof files. To write a HEAP_DUMP or HEAP_DUMP_SEGMENT see HeapDumpWriter.
 * <p/>
 * <p></p><h3>Usage</h3></p>
 * <pre>
 *     HprofWriter writer = new HprofWrite(out);
 *     writer.writeHprofFileHeader("JAVA PROFILE 1.0.1", 4, 0, 0); // Always write the file header first!
 *     for (...) {
 *         // Write hprof records
 *     }
 * </pre>
 * Created by Erik Andre on 13/07/2014.
 */
public class HprofWriter {

    private final OutputStream out;

    public HprofWriter(@Nonnull OutputStream out) {
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
    public void writeHprofFileHeader(@Nonnull String text, int idSize, int timeHigh, int timeLow) throws IOException {
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
     * Write a STRING record.
     *
     * @param string The string to write
     */
    public void writeStringRecord(@Nonnull HprofString string) throws IOException {
        byte[] stringData = string.getValue().getBytes();
        writeRecordHeader(Tag.STRING, string.getTimestamp(), stringData.length + 4);
        writeInt(out, string.getId());
        write(out, stringData);
    }

    /**
     * Write a LOAD_CLASS record, containing some of the fields of a ClassDefinition.
     *
     * @param cls The class definition to write
     * @throws IOException
     */
    public void writeLoadClassRecord(@Nonnull ClassDefinition cls) throws IOException {
        writeRecordHeader(Tag.LOAD_CLASS, cls.getTimestamp(), 16);
        writeInt(out, cls.getSerialNumber());
        writeInt(out, cls.getObjectId());
        writeInt(out, cls.getStackTraceSerial());
        writeInt(out, cls.getNameStringId());
    }

    /**
     * Returns the OutputStream used by this writer.
     *
     * @return The OutputStream
     */
    @Nonnull
    public OutputStream getOutputStream() {
        return out;
    }
}
