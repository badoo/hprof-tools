package com.badoo.hprof.cruncher;

import com.badoo.hprof.library.HprofProcessor;
import com.badoo.hprof.library.HprofReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Erik Andre on 22/10/14.
 */
public class HprofCruncher {

    public static void main(String[] args) {
        OutputStream out = null;
        try {
            out = new FileOutputStream("out.hprof");
            CrunchProcessor processor = new CrunchProcessor(out);
            InputStream in = new FileInputStream("in.hprof");
            HprofReader reader = new HprofReader(in, processor);
            while (reader.hasNext()) {
                reader.next();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
