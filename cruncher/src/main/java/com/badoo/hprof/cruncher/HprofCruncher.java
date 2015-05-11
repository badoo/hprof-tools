package com.badoo.hprof.cruncher;

import com.badoo.hprof.cruncher.util.Stats;
import com.badoo.hprof.library.HprofReader;
import com.google.common.io.CountingOutputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Application for converting HPROF files to the BMD format.
 * <p/>
 * In this process some data is lost:
 * <p/>
 * - Most strings (strings are replaced with hashes that can potentially be reversed with access to the app that generated the dump).
 * - Primitive instance fields.
 * - Stack trace serial numbers and protection domain for instance dumps.
 * <p/>
 * Created by Erik Andre on 22/10/14.
 */
public class HprofCruncher {

    private static final boolean COLLECT_STATS = true; // Turn this off to (slightly) improve performance

    /**
     * Crunch a HPROF file, converting it to BMD format.
     *
     * @param inFile Input file (hprof)
     * @param out    Output (BMD)
     * @throws IOException If an error occurs while writing the output data
     */
    public static void crunch(File inFile, OutputStream out) throws IOException {
        // Wrap in a CountingOutputStream so we can check the final file size later
        CountingOutputStream cOut = new CountingOutputStream(out);
        if (COLLECT_STATS) {
            out = cOut;
        }
        CrunchProcessor processor = new CrunchProcessor(out, true);
        // Start first pass
        InputStream in = new BufferedInputStream(new FileInputStream(inFile));
        HprofReader reader = new HprofReader(in, processor);
        while (reader.hasNext()) {
            reader.next();
        }
        processor.startSecondPass();
        in.close();
        // Start second pass
        in = new BufferedInputStream(new FileInputStream(inFile));
        reader = new HprofReader(in, processor);
        while (reader.hasNext()) {
            reader.next();
        }
        processor.finishAndWriteOutput();
        // Print some stats about the conversion
        printStats(inFile, cOut);
    }

    private static void printStats(File inFile, CountingOutputStream out) {
        if (COLLECT_STATS) {
            {
                final long sizeBefore = inFile.length();
                final long sizeAfter = out.getCount();
                final double ratio = sizeAfter / (double) sizeBefore;
                System.out.printf("Total size, before: %d, after: %d, ratio: %.3f\n", sizeBefore, sizeAfter, ratio);
            }
            {
                final long sizeBefore = Stats.getStat(Stats.StatType.STRING_HPROF);
                final long sizeAfter = Stats.getStat(Stats.StatType.STRING_BMD);
                final double ratio = sizeAfter / (double) sizeBefore;
                System.out.printf("Strings size, before: %d, after: %d, ratio: %.3f\n", sizeBefore, sizeAfter, ratio);
            }
            {
                final long sizeBefore = Stats.getStat(Stats.StatType.CLASS_HPROF);
                final long sizeAfter = Stats.getStat(Stats.StatType.CLASS_BMD);
                final double ratio = sizeAfter / (double) sizeBefore;
                System.out.printf("Classes size, before: %d, after: %d, ratio: %.3f\n", sizeBefore, sizeAfter, ratio);
            }
            {
                final long sizeBefore = Stats.getStat(Stats.StatType.INSTANCE_HPROF);
                final long sizeAfter = Stats.getStat(Stats.StatType.INSTANCE_BMD);
                final double ratio = sizeAfter / (double) sizeBefore;
                System.out.printf("Instances size, before: %d, after: %d, ratio: %.3f\n", sizeBefore, sizeAfter, ratio);
            }
            {
                final long sizeBefore = Stats.getStat(Stats.StatType.ARRAY_HPROF);
                final long sizeAfter = Stats.getStat(Stats.StatType.ARRAY_BMD);
                final double ratio = sizeAfter / (double) sizeBefore;
                System.out.printf("Arrays size, before: %d, after: %d, ratio: %.3f\n", sizeBefore, sizeAfter, ratio);
            }
        }
    }

    public static void main(String[] args) {
        String inFile;
        String outFile;
        if (args != null && args.length >= 2) {
            inFile = args[0];
            outFile = args[1];
        }
        else {
            System.out.println("Usage:");
            System.out.println("java -jar cruncher.jar input.hprof output.bmd");
            return;
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(outFile);
            crunch(new File(inFile), out);
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
