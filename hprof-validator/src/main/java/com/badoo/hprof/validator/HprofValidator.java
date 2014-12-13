package com.badoo.hprof.validator;


import com.badoo.hprof.library.HprofProcessor;
import com.badoo.hprof.library.HprofReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Erik Andre on 12/12/14.
 */
public class HprofValidator {

    public static void main(String[] args) {
        try {
            String fileName = args != null && args.length > 0 ? args[0] : "in.hprof";
            InputStream in = new FileInputStream(fileName);
            ValidatingProcessor processor = new ValidatingProcessor();
            HprofReader reader = new HprofReader(in, processor);
            while (reader.hasNext()) {
                reader.next();
            }
            // All data loaded, start to check that it is consistent
            processor.verifyClasses();
        }
        catch (IOException e) {
           System.err.print("Failed to process file");
            e.printStackTrace(System.err);
        }
    }

}
