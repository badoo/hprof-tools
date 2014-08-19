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

    public static String readString(InputStream in, int length) throws IOException {
        byte[] buffer = new byte[length];
        in.read(buffer);
        return new String(buffer);
    }

    public static void writeString(OutputStream out, String str) throws IOException {
        out.write(str.getBytes());
    }

    public static void writeNullTerminatedString(OutputStream out, String str) throws IOException {
        out.write(str.getBytes());
        out.write(0);
    }

    public static void writeByte(OutputStream out, int value) throws IOException {
        out.write(value);
    }

    public static int readByte(InputStream in) throws IOException {
        return in.read();
    }

    public static int readInt(InputStream in) throws IOException {
        return (in.read() << 24) | (in.read() << 16) | (in.read() << 8) | in.read();
    }

    public static void writeInt(OutputStream out, int value) throws IOException {
        out.write(value >> 24);
        out.write(value >> 16);
        out.write(value >> 8);
        out.write(value);
    }

    public static short readShort(InputStream in) throws IOException {
        return (short) ((in.read() << 8) | in.read());
    }

    public static void writeShort(OutputStream out, short value) throws IOException {
        out.write(value >> 8);
        out.write(value);
    }

    public static byte[] copy(InputStream in, OutputStream out, int size) throws IOException {
        byte buffer[] = new byte[size];
        in.read(buffer);
        out.write(buffer);
        return buffer;
    }

    public static byte[] read(InputStream in, int length) throws IOException {
        byte[] data = new byte[length];
        in.read(data);
        return data;
    }

    public static void write(OutputStream out, byte[] data) throws IOException {
        out.write(data);
    }

}
