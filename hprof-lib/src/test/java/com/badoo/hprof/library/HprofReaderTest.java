package com.badoo.hprof.library;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.processor.DiscardProcessor;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class HprofReaderTest {

    private static final String HEADER_TEXT = "Test hprof 1.0";
    private static final int TIME_HIGH = 12345;
    private static final int TIME_LOW = 67890;
    private static final String STRING_A = "a";
    private static final String STRING_B = "b";
    private static final String STRING_C = "c";
    private static final int SERIAL = 123;
    private static final int OBJECT_ID = 666;
    private static final int STACK_SERIAL = 999;
    private static final int NAME_ID = 2;


    private ByteArrayOutputStream buffer;
    private HprofWriter writer;

    @Before
    public void setup() throws IOException {
        buffer = new ByteArrayOutputStream();
        writer = new HprofWriter(buffer);
        writer.writeHprofFileHeader(HEADER_TEXT, 4, TIME_HIGH, TIME_LOW);
    }

    @Test
    public void readHeader() throws IOException {
        final AtomicBoolean called = new AtomicBoolean(false);
        HprofProcessor processor = new DiscardProcessor() {
            @Override
            public void onHeader(@Nonnull String text, int idSize, int timeHigh, int timeLow) throws IOException {
                called.set(true);
                assertEquals(HEADER_TEXT, text);
                assertEquals(TIME_HIGH, timeHigh);
                assertEquals(TIME_LOW, timeLow);
            }
        };
        HprofReader reader = new HprofReader(new ByteArrayInputStream(buffer.toByteArray()), processor);
        reader.next();
        assertTrue(called.get());
    }

    @Test
    public void readStrings() throws IOException {
        // Setup data
        writer.writeStringRecord(new HprofString(0, STRING_A, 0));
        writer.writeStringRecord(new HprofString(1, STRING_B, 1));
        writer.writeStringRecord(new HprofString(2, STRING_C, 2));
        // Verify
        final AtomicInteger calls = new AtomicInteger(0);
        HprofProcessor processor = new DiscardProcessor() {

            @Override
            public void onRecord(int tag, int timestamp, int length, @Nonnull HprofReader reader) throws IOException {
                assertEquals(Tag.STRING, tag);
                HprofString string = reader.readStringRecord(length, timestamp);
                switch (calls.get()) {
                    case 0:
                        assertEquals(0, timestamp);
                        assertEquals(0, string.getId());
                        assertEquals(STRING_A, string.getValue());
                        break;
                    case 1:
                        assertEquals(1, timestamp);
                        assertEquals(1, string.getId());
                        assertEquals(STRING_B, string.getValue());
                        break;
                    case 2:
                        assertEquals(2, timestamp);
                        assertEquals(2, string.getId());
                        assertEquals(STRING_C, string.getValue());
                        break;
                    default:
                        fail("To many records!");
                }
                calls.incrementAndGet();
            }
        };
        HprofReader reader = new HprofReader(new ByteArrayInputStream(buffer.toByteArray()), processor);
        while (reader.hasNext()) {
            reader.next();
        }
        assertEquals(3, calls.get());
    }

    @Test
    public void readLoadClassRecord() throws IOException {
        ClassDefinition cls = new ClassDefinition();
        cls.setSerialNumber(SERIAL);
        cls.setObjectId(OBJECT_ID);
        cls.setStackTraceSerial(STACK_SERIAL);
        cls.setNameStringId(NAME_ID);
        writer.writeLoadClassRecord(cls);
        // Verify written data
        final AtomicBoolean called = new AtomicBoolean(false);
        HprofProcessor processor = new DiscardProcessor() {

            @Override
            public void onRecord(int tag, int timestamp, int length, @Nonnull HprofReader reader) throws IOException {
                called.set(true);
                ClassDefinition readCls = reader.readLoadClassRecord();
                assertEquals(SERIAL, readCls.getSerialNumber());
                assertEquals(OBJECT_ID, readCls.getObjectId());
                assertEquals(STACK_SERIAL, readCls.getStackTraceSerial());
                assertEquals(NAME_ID, readCls.getNameStringId());
            }
        };
        HprofReader reader = new HprofReader(new ByteArrayInputStream(buffer.toByteArray()), processor);
        while (reader.hasNext()) {
            reader.next();
        }
        assertTrue(called.get());
    }

}