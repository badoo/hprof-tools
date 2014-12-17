package com.badoo.bmd.decruncher;

import com.badoo.bmd.BmdReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Application for converting BMD files to HPROF.
 * <p/>
 * Created by Erik Andre on 02/11/14.
 */
public class BmdDecruncher {

    public static void main(String[] args) {
        try {
            String inFile;
            String outFile;
            List<String> additionalFiles = new ArrayList<String>();
            if (args != null && args.length >= 2) {
                List<String> argList = Arrays.asList(args);
                inFile = argList.remove(0); // args[0]
                outFile = argList.remove(0); // args[1]
                additionalFiles.addAll(argList);
            }
            else {
                inFile = "in.bmd";
                outFile = "out.hprof";
            }
            Set<String> strings = new HashSet<String>();
            for (String file : additionalFiles) {
                if (file.endsWith(".dex") || file.endsWith(".apk")) {
                    strings.addAll(ApkStringReader.readStrings(new File("app.apk")));
                }
                else if (file.endsWith(".jar")) {
                    //TODO
                }
            }
            OutputStream out = new FileOutputStream(outFile);
            DecrunchProcessor processor = new DecrunchProcessor(out, strings);
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
