package com.badoo.hprof.cruncher.bmd;

import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Erik Andre on 22/10/14.
 */
public class BmdWriter extends DataWriter {

    public BmdWriter(OutputStream out) {
        super(out);
    }

    public void writeHeader(int version, @Nullable byte[] metadata) throws IOException {
        writeInt32(version);
        writeByteArrayWithLength(metadata != null? metadata : new byte[]{});
    }

    public void writeStringRecord(int id, String string, boolean hashed) throws IOException {
        writeInt32(hashed? BmdTag.HASHED_STRING : BmdTag.STRING);
        writeInt32(id);
        if (hashed) {
            writeInt32(string.hashCode());
        } else {
            writeByteArrayWithLength(string.getBytes());
        }
    }
}
