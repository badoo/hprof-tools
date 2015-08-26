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
import java.util.ArrayList;
import java.util.List;
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

    /**
     * Configurator for controlling how HprofCruncher converts HPROF files to BMD.
     */
    @SuppressWarnings("UnusedDeclaration")
    public static class Config {

        public static final int NO_TIME_LIMIT = -1;

        private final boolean collectStats;
        private final long timeLimit;
        private final long iterationSleep;
        private final List<PreserveClass> preservedClasses;


        private Config(boolean collectStats, long timeLimit, long iterationSleep, List<PreserveClass> preservedClasses) {
            this.collectStats = collectStats;
            this.timeLimit = timeLimit;
            this.iterationSleep = iterationSleep;
            this.preservedClasses = preservedClasses;
        }

        public static class Builder {

            private boolean stats;
            private long timeLimit = NO_TIME_LIMIT;
            private long iterationSleep;
            private List<PreserveClass> preservedClasses = new ArrayList<PreserveClass>();

            /**
             * Sets whether stats should be collected to measure how well the crunch operation performs
             *
             * @param enabled true if stats should be collected
             */
            public Builder stats(boolean enabled) {
                this.stats = enabled;
                return this;
            }

            /**
             * Sets a time limit for the crunch operation. If the operation goes over time it will be cancelled.
             *
             * @param timeLimit time limit in milliseconds
             */
            public Builder timeLimit(long timeLimit) {
                this.timeLimit = timeLimit;
                return this;
            }

            /**
             * Set the time the thread performing the crunching will spend sleeping for each iteration. This number will dramatically slow down the operation.
             * <p/>
             * Examples:
             * Nexus 5, 200MB HPROF, sleep=10, crunch time=14min
             * Nexus 5, 200MB HPROF, sleep=5, crunch time=8.3min
             * Nexus 5, 200MB HPROF, sleep=1, crunch time=3.5min
             * Nexus 5, 200MB HPROF, sleep=0, crunch time=1.5min
             *
             * @param sleep number of milliseconds to spend sleeping each iteration.
             */
            public Builder iterationSleep(long sleep) {
                this.iterationSleep = sleep;
                return this;
            }

            /**
             * Add a class to the list of preserved classes. These classes will not have any primitive fields
             * removed in order to save space. Objects (including Strings) referenced by instances of the class
             * will still be crunched as usual unless they are also added to the list of preserved classes.
             *
             * @param classToPreserve the class to preserve
             * @return the builder, for chained calls
             */
            public Builder preserveClass(@Nonnull Class classToPreserve) {
                preservedClasses.add(new PreserveClass(classToPreserve.getName()));
                return this;
            }

            /**
             * Add a class name to the list of preserved classes. These classes will not have any primitive fields
             * removed in order to save space. Objects (including Strings) referenced by instances of the class
             * will still be crunched as usual unless they are also added to the list of preserved classes.
             *
             * @param classToPreserve the class to preserve
             * @return the builder, for chained calls
             */
            public Builder preserveClass(@Nonnull String classToPreserve) {
                preservedClasses.add(new PreserveClass(classToPreserve));
                return this;
            }

            public Config build() {
                return new Config(stats, timeLimit, iterationSleep, preservedClasses);
            }
        }

        public static class PreserveClass {

            private final String className;

            public PreserveClass(@Nonnull String className) {
                this.className = className;
            }

            @Nonnull
            public String getClassToPreserve() {
                return className;
            }
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
            config = new Config.Builder().build();
        }
        Stats.setEnabled(config.collectStats);
        Stats.increment(Stats.Type.TOTAL, Stats.Variant.HPROF, source.getDataSize());
        final long start = System.currentTimeMillis();
        final long limit = config.timeLimit != Config.NO_TIME_LIMIT ? start + config.timeLimit : Long.MAX_VALUE;
        // Wrap in a CountingOutputStream so we can check the final file size later
        CountingOutputStream cOut = new CountingOutputStream(out);
        if (config.collectStats) {
            out = cOut;
        }
        CrunchProcessor processor = new CrunchProcessor(out, true);
        // Start first pass
        InputStream in = new BufferedInputStream(source.open());
        try {
            HprofReader reader = new HprofReader(in, processor);
            while (reader.hasNext()) {
                reader.next();
                checkTimeLimit(limit);
                iterationSleep(config);
            }
            processor.startSecondPass();
        }
        finally {
            in.close();
        }
        // Start second pass
        in = new BufferedInputStream(source.open());
        try {
            HprofReader reader = new HprofReader(in, processor);
            while (reader.hasNext()) {
                reader.next();
                checkTimeLimit(limit);
                iterationSleep(config);
            }
            processor.finishAndWriteOutput();
        }
        finally {
            in.close();
        }
        // Print some stats about the conversion
        Stats.increment(Stats.Type.TOTAL, Stats.Variant.BMD, cOut.getCount());
        Stats.printStats();

    }

    private static void iterationSleep(Config config) {
        if (config.iterationSleep == 0) {
            return;
        }
        try {
            Thread.sleep(config.iterationSleep);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            Config config = new Config.Builder().stats(true).build();
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
