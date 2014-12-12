package com.badoo.bmd.decruncher;

import com.badoo.bmd.BmdReader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * TODO Document
 * Created by Erik Andre on 02/11/14.
 */
public class BmdDecruncher {

    public static void main(String[] args) {
        try {
            OutputStream out = new FileOutputStream("out.hprof");
            DecrunchProcessor processor = new DecrunchProcessor(out);
            BmdReader reader = new BmdReader(new FileInputStream("in.bmd"), processor);
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
