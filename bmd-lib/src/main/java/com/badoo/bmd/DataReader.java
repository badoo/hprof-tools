// Protocol Buffers - Google's data interchange format
// Copyright 2008 Google Inc.  All rights reserved.
// http://code.google.com/p/protobuf/
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of Google Inc. nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.badoo.bmd;

import com.badoo.hprof.library.util.StreamUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * Based on https://protobuf.googlecode.com/svn-history/r530/trunk/java/src/main/java/com/google/protobuf/CodedInputStream.java
 * Modified by Badoo 2014 (Erik Andre)
 */

/**
 * Reads and decodes numeric data
 *
 * @author kenton@google.com Kenton Varda
 */
public class DataReader {

    protected final InputStream in;

    public DataReader(InputStream in) {
        this.in = in;
    }

    /** Read a {@code double} field value from the stream. */
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readRawLittleEndian64());
    }

    /** Read a {@code float} field value from the stream. */
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readRawLittleEndian32());
    }

    /** Read an {@code int64} field value from the stream. */
    public long readInt64() throws IOException {
        return readRawVarint64();
    }

    /** Read an {@code int32} field value from the stream. */
    public int readInt32() throws IOException {
        return readRawVarint32();
    }

    /** Read a {@code bool} field value from the stream. */
    public boolean readBool() throws IOException {
        return readRawVarint64() != 0;
    }

    // =================================================================

    /**
     * Read a raw Varint from the stream.  If larger than 32 bits, discard the
     * upper bits.
     */
    public int readRawVarint32() throws IOException {
        byte tmp = readRawByte();
        if (tmp >= 0) {
            return tmp;
        }
        int result = tmp & 0x7f;
        if ((tmp = readRawByte()) >= 0) {
            result |= tmp << 7;
        } else {
            result |= (tmp & 0x7f) << 7;
            if ((tmp = readRawByte()) >= 0) {
                result |= tmp << 14;
            } else {
                result |= (tmp & 0x7f) << 14;
                if ((tmp = readRawByte()) >= 0) {
                    result |= tmp << 21;
                } else {
                    result |= (tmp & 0x7f) << 21;
                    result |= (tmp = readRawByte()) << 28;
                    if (tmp < 0) {
                        // Discard upper 32 bits.
                        for (int i = 0; i < 5; i++) {
                            if (readRawByte() >= 0) {
                                return result;
                            }
                        }
                        throw new IOException();
                    }
                }
            }
        }
        return result;
    }

    /** Read a raw Varint from the stream. */
    public long readRawVarint64() throws IOException {
        int shift = 0;
        long result = 0;
        while (shift < 64) {
            final byte b = readRawByte();
            result |= (long)(b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
            shift += 7;
        }
        throw new IOException();
    }

    /**
     * Read one byte from the input.
     */
    public byte readRawByte() throws IOException {
        return (byte) in.read();
    }

    /**
     * Read a fixed size of bytes from the input.
     */
    public byte[] readRawBytes(final int size) throws IOException {
        return StreamUtil.read(in, size);
    }

    /** Read a 32-bit little-endian integer from the stream. */
    public int readRawLittleEndian32() throws IOException {
        final byte b1 = readRawByte();
        final byte b2 = readRawByte();
        final byte b3 = readRawByte();
        final byte b4 = readRawByte();
        return (((int)b1 & 0xff)      ) |
            (((int)b2 & 0xff) <<  8) |
            (((int)b3 & 0xff) << 16) |
            (((int)b4 & 0xff) << 24);
    }

    /** Read a 64-bit little-endian integer from the stream. */
    public long readRawLittleEndian64() throws IOException {
        final byte b1 = readRawByte();
        final byte b2 = readRawByte();
        final byte b3 = readRawByte();
        final byte b4 = readRawByte();
        final byte b5 = readRawByte();
        final byte b6 = readRawByte();
        final byte b7 = readRawByte();
        final byte b8 = readRawByte();
        return (((long)b1 & 0xff)      ) |
            (((long)b2 & 0xff) <<  8) |
            (((long)b3 & 0xff) << 16) |
            (((long)b4 & 0xff) << 24) |
            (((long)b5 & 0xff) << 32) |
            (((long)b6 & 0xff) << 40) |
            (((long)b7 & 0xff) << 48) |
            (((long)b8 & 0xff) << 56);
    }

}