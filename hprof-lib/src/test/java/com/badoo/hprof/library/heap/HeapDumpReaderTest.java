package com.badoo.hprof.library.heap;

import com.badoo.hprof.library.heap.processor.HeapDumpDiscardProcessor;
import com.badoo.hprof.library.model.ClassDefinition;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class HeapDumpReaderTest {

    private static final int OBJECT_ID = 1;
    private static final int STACK_TRACE_SERIAL = 2;
    private static final int SUPER_CLASS_OBJECT_ID = 3;
    private static final int CLASS_LOADER_OBJECT_ID = 4;
    private static final int SIGNERS_OBJECT_ID = 5;
    private static final int PROTECTION_DOMAIN_OBJECT_ID = 6;
    private static final int INSTANCE_SIZE = 7;


    @Test
    public void readClassDump() throws IOException {
        // Write data
        ClassDefinition srcClass = new ClassDefinition();
        srcClass.setObjectId(OBJECT_ID);
        srcClass.setStackTraceSerial(STACK_TRACE_SERIAL);
        srcClass.setSuperClassObjectId(SUPER_CLASS_OBJECT_ID);
        srcClass.setClassLoaderObjectId(CLASS_LOADER_OBJECT_ID);
        srcClass.setSignersObjectId(SIGNERS_OBJECT_ID);
        srcClass.setProtectionDomainObjectId(PROTECTION_DOMAIN_OBJECT_ID);
        srcClass.setInstanceSize(INSTANCE_SIZE);
        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
        HeapDumpWriter writer = new HeapDumpWriter(outBuffer);
        writer.writeClassDumpRecord(srcClass);
        // Verify
        final AtomicBoolean called = new AtomicBoolean(false);
        byte[] data = outBuffer.toByteArray();
        ByteArrayInputStream inBuffer = new ByteArrayInputStream(data);
        HeapDumpProcessor processor = new HeapDumpDiscardProcessor() {
            @Override
            public void onHeapRecord(int tag, HeapDumpReader reader) throws IOException {
                called.set(true);
                Map<Integer, ClassDefinition> loadedClasses = new HashMap<Integer, ClassDefinition>();
                loadedClasses.put(OBJECT_ID, new ClassDefinition());
                ClassDefinition dstClass = reader.readClassDumpRecord(loadedClasses);
                assertEquals(OBJECT_ID, dstClass.getObjectId());
                assertEquals(STACK_TRACE_SERIAL, dstClass.getStackTraceSerial());
                assertEquals(SUPER_CLASS_OBJECT_ID, dstClass.getSuperClassObjectId());
                assertEquals(CLASS_LOADER_OBJECT_ID, dstClass.getClassLoaderObjectId());
                assertEquals(SIGNERS_OBJECT_ID, dstClass.getSignersObjectId());
                assertEquals(PROTECTION_DOMAIN_OBJECT_ID, dstClass.getProtectionDomainObjectId());
                assertEquals(INSTANCE_SIZE, dstClass.getInstanceSize());
            }
        };
        HeapDumpReader reader = new HeapDumpReader(inBuffer, data.length, processor);
        while (reader.hasNext()) {
            reader.next();
        }
        assertTrue(called.get());
    }

}