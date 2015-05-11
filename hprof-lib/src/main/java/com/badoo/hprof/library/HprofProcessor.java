package com.badoo.hprof.library;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Callback interface used by HprofReader to notify that data is being read.
 * <p/>
 * Created by Erik Andre on 12/07/2014.
 */
public interface HprofProcessor {

    /**
     * Callback method invoked when the HPROF header is read.
     *
     * @param text     String representing the format name and version, in this implementation and historically, the string "JAVA PROFILE 1.0.1" (18 u1 bytes) followed by a NULL byte. If the TAG "HEAP DUMP SEGMENT" is used this string will be "JAVA PROFILE 1.0.2".
     * @param idSize   Size of identifiers (always 4 in Android). Identifiers are used to represent UTF8 strings, objects, stack traces, etc. They can have the same size as host pointers or sizeof(void*), but are not required to be.
     * @param timeHigh High word of number of milliseconds since 0:00 GMT, 1/1/70
     * @param timeLow  Low word of number of milliseconds since 0:00 GMT, 1/1/70
     */
    void onHeader(@Nonnull String text, int idSize, int timeHigh, int timeLow) throws IOException;

    /**
     * Callback method invoked when a record is read.
     *
     * @param tag       A tag indicating what type of record it is (See Tag)
     * @param timestamp Number of microseconds since the timestamp in the header
     * @param length    Number of bytes in the record (excluding the record header)
     * @param reader    The reader from which the rest of the record can be read
     */
    void onRecord(int tag, int timestamp, int length, @Nonnull HprofReader reader) throws IOException;

}
