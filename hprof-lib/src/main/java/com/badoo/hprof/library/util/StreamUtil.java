package com.badoo.hprof.library.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.annotation.Nonnull;

/**
 * Utility class containing methods to read and write to streams.
 * <p/>
 * Created by Erik Andre on 13/07/2014.
 */
@SuppressWarnings("UnusedDeclaration")
public class StreamUtil {

    private static final byte[] buffer = new byte[1024];

    /**
     * Read a null-terminated string.
     *
     * @param in The InputStream to read the string from
     * @return The string
     * @throws IOException
     */
    public static String readNullTerminatedString(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte b;
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
        byte[] buffer = read(in, length);
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
     * Read an long.
     *
     * @param in The InputStream to read the int from
     * @return The long read
     * @throws IOException
     */
    public static long readLong(InputStream in) throws IOException {
        return ((long) in.read() << 56) | ((long) in.read() << 48) | ((long) in.read() << 40) | ((long) in.read() << 32) | ((long) in.read() << 24) | ((long) in.read() << 16) | ((long) in.read() << 8) | (long) in.read();
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
     * Read a double value from an InputStream
     *
     * @param in stream to read the value from
     * @return the double read
     */
    public static double readDouble(@Nonnull InputStream in) throws IOException {
        long longBits = readLong(in);
        return Double.longBitsToDouble(longBits);
    }

    /**
     * Read a float value from an InputStream
     *
     * @param in stream to read the value from
     * @return the float read
     */
    public static float readFloat(@Nonnull InputStream in) throws IOException {
        int intBits = readInt(in);
        return Float.intBitsToFloat(intBits);
    }

    /**
     * Copy a number of bytes from one stream to another, returning a copy of the bytes read.
     *
     * @param in     The InputStream
     * @param out    The OutputStream
     * @param length Number of bytes to read
     * @return An array containing the bytes read
     * @throws IOException
     */
    public static byte[] copy(InputStream in, OutputStream out, int length) throws IOException {
        byte buffer[] = read(in, length);
        write(out, buffer);
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
        int read = 0;
        while (read != length) {
            read += in.read(data, read, length - read);
        }
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

    /**
     * Write a repeated byte value.
     *
     * @param out   The OutputStream to write to
     * @param value The value to write
     * @param count Number of times to write the value
     */
    public static void write(final OutputStream out, final int value, final int count) throws IOException {
        if (count > buffer.length) {
            // Fallback
            for (int i = 0; i < count; i++) {
                writeByte(out, value);
            }
        }
        else {
            Arrays.fill(buffer, (byte) value);
            out.write(buffer, 0, count);
        }
    }

    /**
     * Skip a number of bytes from an InputStream.
     *
     * @param in     The InputStream to read from
     * @param length The number of bytes to skip
     */
    public static void skip(final InputStream in, final int length) throws IOException {
        int skipped = 0;
        while (skipped != length) {
            skipped += in.skip(length - skipped);
        }
    }

}
