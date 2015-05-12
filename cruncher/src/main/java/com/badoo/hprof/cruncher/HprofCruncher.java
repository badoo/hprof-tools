package com.badoo.hprof.cruncher;

import com.badoo.hprof.cruncher.util.Stats;
import com.badoo.hprof.library.HprofReader;
import com.google.common.io.CountingOutputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    @SuppressWarnings("UnusedDeclaration")
    public static class Config {

        public static final int NO_TIME_LIMIT = -1;

        private boolean collectStats;
        private long timeLimit = NO_TIME_LIMIT;

        /**
         * Sets whether stats should be collected to measure how well the crunch operation performs
         *
         * @param enabled true if stats should be collected
         */
        public Config enableStats(boolean enabled) {
            this.collectStats = enabled;
            return this;
        }

        /**
         * Sets a time limit for the crunch operation. If the operation goes over time it will be cancelled.
         *
         * @param timeLimit time limit in milliseconds
         */
        public Config setTimeLimit(long timeLimit) {
            this.timeLimit = timeLimit;
            return this;
        }
    }

    /**
     * Crunch a HPROF file, converting it to BMD format.
     *
     * @param source the HPROF data source
     * @param out    Output (BMD)
     * @throws IOException If an error occurs while writing the output data
     */
    public static void crunch(@Nonnull HprofSource source, @Nonnull OutputStream out, @Nullable Config config) throws IOException, TimeoutException {
        if (config == null) {
            config = new Config();
        }
        Stats.setEnabled(config.collectStats);
        Stats.increment(Stats.Type.TOTAL, Stats.Variant.HPROF, source.getDataSize());
        final long start = System.currentTimeMillis();
        final long limit = config.timeLimit != Config.NO_TIME_LIMIT? start + config.timeLimit : Long.MAX_VALUE;
        // Wrap in a CountingOutputStream so we can check the final file size later
        CountingOutputStream cOut = new CountingOutputStream(out);
        if (config.collectStats) {
            out = cOut;
        }
        CrunchProcessor processor = new CrunchProcessor(out, true);
        // Start first pass
        InputStream in = new BufferedInputStream(source.open());
        HprofReader reader = new HprofReader(in, processor);
        while (reader.hasNext()) {
            reader.next();
            checkTimeLimit(limit);
        }
        processor.startSecondPass();
        in.close();
        // Start second pass
        in = new BufferedInputStream(source.open());
        reader = new HprofReader(in, processor);
        while (reader.hasNext()) {
            reader.next();
            checkTimeLimit(limit);
        }
        processor.finishAndWriteOutput();
        // Print some stats about the conversion
        Stats.increment(Stats.Type.TOTAL, Stats.Variant.BMD, cOut.getCount());
        Stats.printStats();
    }

    private static void checkTimeLimit(long limit) throws TimeoutException {
        if (System.currentTimeMillis() > limit) {
            throw new TimeoutException("Crunching operation took too long!");
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
            System.err.println("Usage:");
            System.err.println("java -jar cruncher.jar input.hprof output.bmd");
            System.exit(1);
            return;
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(outFile);
            Config config = new Config();
            config.enableStats(true);
            crunch(new HprofFileSource(new File(inFile)), out, config);
            System.exit(0);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
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
