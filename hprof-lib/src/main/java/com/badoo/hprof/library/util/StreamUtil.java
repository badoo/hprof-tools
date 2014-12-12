package com.badoo.hprof.library.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class containing methods to read and write to streams.
 * <p/>
 * Created by Erik Andre on 13/07/2014.
 */
public class StreamUtil {

    /**
     * Read a null-terminated string.
     *
     * @param in The InputStream to read the string from
     * @return The string
     * @throws IOException
     */
    public static String readNullTerminatedString(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte b = 0;
        do {
            b = (byte) in.read();
            if (b != 0) {
                buffer.write(b);
            }
        } while (b != 0);
        return new String(buffer.toByteArray());
    }

    /**
     * Read a string of known length.
     *
     * @param in     The InputStream to read the string from
     * @param length The length of the string in bytes
     * @return The string
     * @throws IOException
     */
    public static String readString(InputStream in, int length) throws IOException {
        byte[] buffer = new byte[length];
        in.read(buffer);
        return new String(buffer);
    }

    /**
     * Write a string (not null-terminated).
     *
     * @param out The OutputStream to write the string to
     * @param str The string to write
     * @throws IOException
     */
    public static void writeString(OutputStream out, String str) throws IOException {
        out.write(str.getBytes());
    }

    /**
     * Write a null-terminated string.
     *
     * @param out The OutputStream to write the string to
     * @param str The string to write
     * @throws IOException
     */
    public static void writeNullTerminatedString(OutputStream out, String str) throws IOException {
        out.write(str.getBytes());
        out.write(0);
    }

    /**
     * Write a byte.
     *
     * @param out   The OutputStream to write the byte to
     * @param value The byte to write
     * @throws IOException
     */
    public static void writeByte(OutputStream out, int value) throws IOException {
        out.write(value);
    }

    /**
     * Read a byte.
     *
     * @param in The InputStream to read the byte from
     * @return The byte read
     * @throws IOException
     */
    public static int readByte(InputStream in) throws IOException {
        return in.read();
    }

    /**
     * Read an int.
     *
     * @param in The InputStream to read the int from
     * @return The int read
     * @throws IOException
     */
    public static int readInt(InputStream in) throws IOException {
        return (in.read() << 24) | (in.read() << 16) | (in.read() << 8) | in.read();
    }

    /**
     * Write an int.
     *
     * @param out   The OutputStream to write the int to
     * @param value The int to write
     */
    public static void writeInt(OutputStream out, int value) throws IOException {
        out.write(value >> 24);
        out.write(value >> 16);
        out.write(value >> 8);
        out.write(value);
    }

    /**
     * Write an long.
     *
     * @param out   The OutputStream to write the long to
     * @param value The long to write
     */
    public static void writeLong(OutputStream out, long value) throws IOException {
        out.write((int) (0xff & (value >> 56)));
        out.write((int) (0xff & (value >> 48)));
        out.write((int) (0xff & (value >> 40)));
        out.write((int) (0xff & (value >> 32)));
        out.write((int) (0xff & (value >> 24)));
        out.write((int) (0xff & (value >> 16)));
        out.write((int) (0xff & (value >> 8)));
        out.write((int) value);
    }

    /**
     * Read an short.
     *
     * @param in The InputStream to read the short from
     * @return The short read
     * @throws IOException
     */
    public static short readShort(InputStream in) throws IOException {
        return (short) ((in.read() << 8) | in.read());
    }

    /**
     * Write a short.
     *
     * @param out   The OutputStream to write the short to
     * @param value The short to write
     * @throws IOException
     */
    public static void writeShort(OutputStream out, short value) throws IOException {
        out.write(value >> 8);
        out.write(value);
    }

    /**
     * Copy a number of bytes from one stream to another, returning a copy of the bytes read.
     *
     * @param in   The InputStream
     * @param out  The OutputStream
     * @param size Number of bytes to read
     * @return An array containing the bytes read
     * @throws IOException
     */
    public static byte[] copy(InputStream in, OutputStream out, int size) throws IOException {
        byte buffer[] = new byte[size];
        in.read(buffer);
        out.write(buffer);
        return buffer;
    }

    /**
     * Read a number of bytes.
     *
     * @param in     The InputStream to read from
     * @param length Number of bytes to read
     * @return An array containing the bytes read
     * @throws IOException
     */
    public static byte[] read(InputStream in, int length) throws IOException {
        byte[] data = new byte[length];
        in.read(data);
        return data;
    }

    /**
     * Write a number of bytes
     *
     * @param out  The OutputStream to write to
     * @param data The bytes to write
     * @throws IOException
     */
    public static void write(OutputStream out, byte[] data) throws IOException {
        out.write(data);
    }

}
