package com.badoo.bmd.decruncher;

import com.badoo.bmd.BmdReader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Application for converting BMD files to HPROF.
 *
 * Created by Erik Andre on 02/11/14.
 */
public class BmdDecruncher {

    public static void main(String[] args) {
        String inFile;
        String outFile;
        if (args != null && args.length >= 2) {
            inFile = args[0];
            outFile = args[1];
        }
        else {
            inFile = "in.bmd";
            outFile = "out.hprof";
        }
        try {
            OutputStream out = new FileOutputStream(outFile);
            DecrunchProcessor processor = new DecrunchProcessor(out);
            BmdReader reader = new BmdReader(new FileInputStream(inFile), processor);
            while (reader.hasNext()) {
                reader.next();
            }
            // Write all the heap records collected by the processor
            processor.writeHeapRecords();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
