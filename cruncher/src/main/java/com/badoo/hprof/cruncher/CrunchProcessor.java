package com.badoo.hprof.cruncher;

import com.badoo.hprof.cruncher.bmd.BmdTag;
import com.badoo.hprof.cruncher.bmd.DataWriter;
import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.Tag;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.processor.DiscardProcessor;
import com.badoo.hprof.library.util.StreamUtil;
import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

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

        public void writeLegacyRecord(int tag, byte[] data) throws IOException {
            writeInt32(BmdTag.LEGACY_HPROF_RECORD);
            writeInt32(tag);
            writeRawBytes(data);
        }
    }

    private final CrunchBdmWriter writer;
    private int nextStringId;
    private Map<Integer, Integer> stringIds = new HashMap<Integer, Integer>(); // Maps original to updated string ids

    public CrunchProcessor(OutputStream out) {
        this.writer = new CrunchBdmWriter(out);
    }

    @Override
    public void onRecord(int tag, int timestamp, int length, HprofReader reader) throws IOException {
        switch (tag) {
            case Tag.STRING:
                HprofString string = reader.readStringRecord(length, timestamp);
                // We replace the original string id with one starting from 0 as these are more efficient to store
                stringIds.put(string.getId(), nextStringId); // Save the original id so we can update references later
                string.setId(nextStringId);
                nextStringId++;
                writer.writeString(string, true);
                break;
            case Tag.HEAP_DUMP:
            case Tag.HEAP_DUMP_SEGMENT:
                // TODO Process
                super.onRecord(tag, timestamp, length, reader);
                break;
            case Tag.UNLOAD_CLASS:
            case Tag.HEAP_DUMP_END:
                super.onRecord(tag, timestamp, length, reader); // These records can be discarded
                break;
            default:
                byte[] data = StreamUtil.read(reader.getInputStream(), length);
                writer.writeLegacyRecord(tag, data);
                break;
        }
    }

    @Override
    public void onHeader(String text, int idSize, int timeHigh, int timeLow) throws IOException {
        writer.writeHeader(1, text.getBytes());
    }
}
