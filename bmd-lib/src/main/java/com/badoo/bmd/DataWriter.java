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

/**
 * Based on CodedOutputStream.java (https://code.google.com/p/protobuf/source/browse/trunk/java/src/main/java/com/google/protobuf/CodedOutputStream.java)
 * Modified by Badoo 2014
 */

package com.badoo.bmd;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Based on https://protobuf.googlecode.com/svn-history/r530/trunk/java/src/main/java/com/google/protobuf/CodedOutputStream.java
 * Modified by Badoo 2014, Erik Andre
 *
 * Encodes and writes protocol message fields.
 *
 * @author kneton@google.com Kenton Varda
 */
public abstract class DataWriter {

    protected final OutputStream out;

    protected DataWriter(OutputStream out) {
        this.out = out;
    }

    /**
     * Returns the underlying OutputStream.
     *
     * @return the OutputStream
     */
    public OutputStream getOutputStream() {
        return out;
    }

    /**
     * Write a {@code double} field to the stream.
     */
    protected void writeDouble(final double value) throws IOException {
        writeRawLittleEndian64(Double.doubleToRawLongBits(value));
    }

    /**
     * Write a {@code float} field to the stream.
     */
    protected void writeFloat(final float value) throws IOException {
        writeRawLittleEndian32(Float.floatToRawIntBits(value));
    }

    /**
     * Write an {@code int64} field to the stream.
     */
    protected void writeInt64(final long value) throws IOException {
        writeRawVarint64(value);
    }

    /**
     * Write an {@code int32} field to the stream.
     */
    protected void writeInt32(final int value) throws IOException {
        if (value >= 0) {
            writeRawVarint32(value);
        }
        else {
            // Must sign-extend.
            writeRawVarint64(value);
        }
    }

    /**
     * Write a {@code bytes} field to the stream.
     */
    protected void writeByteArrayWithLength(final byte[] value) throws IOException {
        writeRawVarint32(value.length);
        writeRawBytes(value);
    }

    /**
     * Write a single byte.
     */
    protected void writeRawByte(final byte value) throws IOException {
        out.write(value);
    }

    /**
     * Write a single byte, represented by an integer value.
     */
    protected void writeRawByte(final int value) throws IOException {
        writeRawByte((byte) value);
    }

    /**
     * Write an array of bytes.
     */
    protected void writeRawBytes(final byte[] value) throws IOException {
        writeRawBytes(value, 0, value.length);
    }

    /**
     * Write part of an array of bytes.
     */
    protected void writeRawBytes(final byte[] value, int offset, int length) throws IOException {
        out.write(value, offset, length);
    }

    /**
     * Encode and write a varint.  {@code value} is treated as
     * unsigned, so it won't be sign-extended if negative.
     */
    protected void writeRawVarint32(int value) throws IOException {
        while (true) {
            if ((value & ~0x7F) == 0) {
                writeRawByte(value);
                return;
            }
            else {
                writeRawByte((value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    }

    /**
     * Encode and write a varint.
     */
    protected void writeRawVarint64(long value) throws IOException {
        while (true) {
            if ((value & ~0x7FL) == 0) {
                writeRawByte((int) value);
                return;
            }
            else {
                writeRawByte(((int) value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    }

    /**
     * Write a little-endian 32-bit integer.
     */
    protected void writeRawLittleEndian32(final int value) throws IOException {
        writeRawByte((value) & 0xFF);
        writeRawByte((value >> 8) & 0xFF);
        writeRawByte((value >> 16) & 0xFF);
        writeRawByte((value >> 24) & 0xFF);
    }

    /**
     * Write a little-endian 64-bit integer.
     */
    protected void writeRawLittleEndian64(final long value) throws IOException {
        writeRawByte((int) (value) & 0xFF);
        writeRawByte((int) (value >> 8) & 0xFF);
        writeRawByte((int) (value >> 16) & 0xFF);
        writeRawByte((int) (value >> 24) & 0xFF);
        writeRawByte((int) (value >> 32) & 0xFF);
        writeRawByte((int) (value >> 40) & 0xFF);
        writeRawByte((int) (value >> 48) & 0xFF);
        writeRawByte((int) (value >> 56) & 0xFF);
    }

}