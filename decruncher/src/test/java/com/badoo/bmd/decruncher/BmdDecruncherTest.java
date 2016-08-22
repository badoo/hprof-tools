package com.badoo.bmd.decruncher;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;

import static org.junit.Assert.*;

public class BmdDecruncherTest {

    @Test
    public void testDecrunch() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BmdDecruncher.decrunch(new FileInputStream("../test_files/decrunch_test_in.bmd"), out, Collections.<String>emptyList());
        verify(new FileInputStream("../test_files/decrunch_test_out.hprof"), new ByteArrayInputStream(out.toByteArray()));
    }

    private void verify(InputStream expected, InputStream actual) throws IOException {
        expected = new BufferedInputStream(expected);
        actual = new BufferedInputStream(actual);
        long position = 0;
        int a = expected.read();
        int b = actual.read();
        while (a != -1 && b != -1) {
            if (a != b) {
                fail("Files are different at position " + position + ", expected " + a + " was " + b);
            }
            position++;
            a = expected.read();
            b = actual.read();
        }
    }
}