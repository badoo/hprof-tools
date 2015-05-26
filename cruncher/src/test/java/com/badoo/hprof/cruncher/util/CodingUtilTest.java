package com.badoo.hprof.cruncher.util;

import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class CodingUtilTest {

    @Test
    public void testReadLong() throws Exception {
        // Zero
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.asLongBuffer().put(0);
        assertEquals(0, CodingUtil.readLong(buffer.array()));
        // Max value
        buffer.position(0);
        buffer.asLongBuffer().put(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, CodingUtil.readLong(buffer.array()));
        // Odd value
        buffer.position(0);
        buffer.asLongBuffer().put(46517);
        assertEquals(46517, CodingUtil.readLong(buffer.array()));
        // Min value
        buffer.position(0);
        buffer.asLongBuffer().put(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, CodingUtil.readLong(buffer.array()));
    }

    @Test
    public void testReadInt() throws Exception {
        // Zero
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.asIntBuffer().put(0);
        assertEquals(0, CodingUtil.readInt(buffer.array()));
        // Max value
        buffer.position(0);
        buffer.asIntBuffer().put(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, CodingUtil.readInt(buffer.array()));
        // Odd value
        buffer.position(0);
        buffer.asIntBuffer().put(46517);
        assertEquals(46517, CodingUtil.readInt(buffer.array()));
        // Min value
        buffer.position(0);
        buffer.asIntBuffer().put(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, CodingUtil.readInt(buffer.array()));
    }

    @Test
    public void testReadShort() throws Exception {
        // Zero
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.asShortBuffer().put((short) 0);
        assertEquals(0, CodingUtil.readShort(buffer.array()));
        // Mid value
        buffer.position(0);
        buffer.asShortBuffer().put((short) (Short.MAX_VALUE / 2));
        assertEquals(Short.MAX_VALUE / 2, CodingUtil.readShort(buffer.array()));
        // Max value
        buffer.position(0);
        buffer.asShortBuffer().put(Short.MAX_VALUE);
        assertEquals(Short.MAX_VALUE, CodingUtil.readShort(buffer.array()));
        // Odd value
        buffer.position(0);
        buffer.asShortBuffer().put((short) 4617);
        assertEquals(4617, CodingUtil.readShort(buffer.array()));
        // Min value
        buffer.position(0);
        buffer.asShortBuffer().put(Short.MIN_VALUE);
        assertEquals(Short.MIN_VALUE, CodingUtil.readShort(buffer.array()));
    }
}