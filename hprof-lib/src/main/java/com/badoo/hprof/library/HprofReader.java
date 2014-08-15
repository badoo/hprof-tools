package com.badoo.hprof.library;

import com.badoo.hprof.library.model.ClassDefinition;

import java.io.IOException;
import java.io.InputStream;

import static com.badoo.hprof.library.StreamUtil.readInt;

/**
 * Created by Erik Andre on 12/07/2014.
 */
public class HprofReader {

    private final InputStream in;
    private final HprofProcessor processor;
    private int readCount;

    public HprofReader(InputStream in, HprofProcessor processor) {
        this.in = in;
        this.processor = processor;
    }

    /**
     * Returns true if there is more data to be read (You can call next())
     *
     * @return True if there is more data to be read.
     * @throws IOException
     */
    public boolean hasNext() throws IOException {
        return in.available() > 0;
    }

    /**
     * Read the next record. This will trigger a callback to the processor.
     *
     * @throws IOException
     */
    public void next() throws IOException {
        if (readCount == 0) { // Read header first
            readHeader();
        }
        else {
            readRecord();
        }
        readCount++;
    }

    public InputStream getInputStream() {
        return in;
    }

    /**
     * Read a LOAD_CLASS record and create a class definition for the loaded class.
     *
     * @return A ClassDefinition with some fields filled in (Serial number, class object id, stack trace serial & class name string id)
     * @throws IOException
     */
    public ClassDefinition readLoadClassRecord() throws IOException {
        int serialNumber = readInt(in);
        int classObjectId = readInt(in);
        int stackTraceSerial = readInt(in);
        int classNameStringId = readInt(in);
        return new ClassDefinition(serialNumber, classObjectId, stackTraceSerial, classNameStringId);
    }

    private void readRecord() throws IOException {
        int tagValue = in.read(); // 1 byte tag, see definitions in Tag
        int time = readInt(in);
        int size = readInt(in);
        processor.onRecord(tagValue, time, size, this);
    }

    private void readHeader() throws IOException {
        String text = StreamUtil.readNullTerminatedString(in);
        int idSize = readInt(in);
        int timeHigh = readInt(in);
        int timeLow = readInt(in);
        processor.onHeader(text, idSize, timeHigh, timeLow);
    }

}
