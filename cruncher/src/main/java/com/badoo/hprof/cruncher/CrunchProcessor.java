package com.badoo.hprof.cruncher;

import com.badoo.hprof.cruncher.bmd.BmdTag;
import com.badoo.hprof.cruncher.bmd.DataWriter;
import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.Tag;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.processor.DiscardProcessor;
import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Erik Andre on 22/10/14.
 */
public class CrunchProcessor extends DiscardProcessor {

    private class CrunchBdmWriter extends DataWriter {

        protected CrunchBdmWriter(OutputStream out) {
            super(out);
        }

        public void writeHeader(int version, @Nullable byte[] metadata) throws IOException {
            writeInt32(version);
            writeByteArrayWithLength(metadata != null? metadata : new byte[]{});
        }

        public void writeString(HprofString string, boolean hashed) throws IOException {
            writeInt32(hashed? BmdTag.HASHED_STRING : BmdTag.STRING);
            writeInt32(string.getId());
            if (hashed) {
                writeInt32(string.getValue().hashCode());
            } else {
                writeByteArrayWithLength(string.getValue().getBytes());
            }
        }
    }

    private final CrunchBdmWriter writer;
    private int nextStringId;

    public CrunchProcessor(OutputStream out) {
        this.writer = new CrunchBdmWriter(out);
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, HprofReader reader) throws IOException {
        switch (tag) {
            case Tag.STRING:
                HprofString string = reader.readStringRecord(length, timestamp);
                string.setId(nextStringId);
                nextStringId++;
                System.out.println("Read string: " + string.getId());
                writer.writeString(string, true);
                break;
            default:
                //TODO Wrap into legacy record
                super.onRecord(tag, timestamp, length, reader);
        }
    }

    @Override
    public void onHeader(String text, int idSize, int timeHigh, int timeLow) throws IOException {
        writer.writeHeader(1, text.getBytes());
    }
}
