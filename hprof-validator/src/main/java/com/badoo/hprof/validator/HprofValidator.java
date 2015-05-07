package com.badoo.hprof.validator;


import com.badoo.hprof.library.HprofReader;
import com.google.common.io.CountingInputStream;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * An application that takes an HPROF file as input and performs a simple verification to check that it contains valid HPROF data.
 *
 * Created by Erik Andre on 12/12/14.
 */
public class HprofValidator {

    public static void main(String[] args) {
        try {
            String fileName = args != null && args.length > 0 ? args[0] : "in.hprof";
            CountingInputStream in = new CountingInputStream(new FileInputStream(fileName));
            ValidatingProcessor processor = new ValidatingProcessor(in);
            HprofReader reader = new HprofReader(in, processor);
            while (reader.hasNext()) {
                reader.next();
            }
            // All data loaded, start to check that it is consistent
            processor.verifyClasses();
            processor.verifyInstances();
        }
        catch (IOException e) {
           System.err.print("Failed to process file");
            e.printStackTrace(System.err);
        }
    }

}
