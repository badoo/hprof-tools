package com.badoo.hprof.library;

import java.io.IOException;
import java.io.OutputStream;

import static com.badoo.hprof.library.StreamUtil.writeInt;
import static com.badoo.hprof.library.StreamUtil.writeNullTerminatedString;

/**
 * Class containing methods for writing hprof files
 *
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
     * @param text A magic text identifying the version and type of hprof file
     * @param idSize Size in bytes for all ID fields
     * @param timeHigh High four bytes of the file timestamp, all other 2-byte timestamps in the file are relative to this
     * @param timeLow Low four bytes of the file timestamp
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
     * @param tag The tag, see definitions in Tag
     * @param timestamp Timestamp for the record
     * @param length Length in bytes of the record (not including the header)
     * @throws IOException
     */
    public void writeRecordHeader(int tag, int timestamp, int length) throws IOException {
        out.write(tag);
        writeInt(out, timestamp);
        writeInt(out, length);
    }

}
