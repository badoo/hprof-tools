package com.badoo.hprof.cruncher.util;

/**
 * Utility class for encode/decode operations.
 * <p/>
 * Created by Erik Andre on 02/11/14.
 */
public class CodingUtil {

    /**
     * Reads a long value from a byte buffer.
     *
     * @param buffer the buffer to read from.
     * @return a long value
     */
    public static long readLong(byte[] buffer) {
        return ((((long) buffer[7] & 0xffL)) |
            (((long) buffer[6] & 0xffL) << 8) |
            (((long) buffer[5] & 0xffL) << 16) |
            (((long) buffer[4] & 0xffL) << 24) |
            (((long) buffer[3] & 0xffL) << 32) |
            (((long) buffer[2] & 0xffL) << 40) |
            (((long) buffer[1] & 0xffL) << 48) |
            (((long) buffer[0] & 0xffL) << 56));
    }

    /**
     * Reads a integer value from a byte buffer.
     *
     * @param buffer the buffer to read from.
     * @return an int value
     */
    public static int readInt(byte[] buffer) {
        return (((buffer[3] & 0xff)) |
            ((buffer[2] & 0xff) << 8) |
            ((buffer[1] & 0xff) << 16) |
            ((buffer[0] & 0xff) << 24));
    }

    /**
     * Reads a short value from a byte buffer.
     *
     * @param buffer the buffer to read from.
     * @return a short value
     */
    public static short readShort(byte[] buffer) {
        return (short) (((buffer[1] & 0xff)) |
            ((buffer[0] & 0xff) << 8));
    }

}
