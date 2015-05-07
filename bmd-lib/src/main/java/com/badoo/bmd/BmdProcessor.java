package com.badoo.bmd;

import java.io.IOException;

/**
 * Callback interface used by BmdReader to notify when a new record or header is available to process.
 * <p/>
 * Created by Erik Andre on 02/11/14.
 */
public interface BmdProcessor {

    /**
     * Callback method invoked when a record is read. The reader, and stream, are positioned after
     * the record header when this method is invoked.
     *
     * @param tag    The tag of the record (as defined in BmdTag)
     * @param reader The reader used to read the BMD data.
     */
    public void onRecord(int tag, BmdReader reader) throws IOException;

    /**
     * Callback method invoked when the file header is read. Will only occur once per file.
     *
     * @param version Version number identifying which version of the BMD file format that the data complies to.
     * @param data    Header data (if converted from HPROF this will be the original HPROF header)
     */
    public void onHeader(int version, byte[] data) throws IOException;
}
