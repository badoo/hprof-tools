package com.badoo.hprof.cruncher.library;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import com.badoo.hprof.cruncher.HprofCruncher;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class CruncherService extends IntentService {

    private static final String TAG = "CruncherService";
    private static final String ACTION_CRUNCH_FILE = "com.badoo.hprof.cruncher.library.action.CRUNCH_FILE";
    private static final String ACTION_CHECK_FILES = "com.badoo.hprof.cruncher.library.action.CHECK_FILES";
    private static final String EXTRA_INPUT_FILE = "com.badoo.hprof.cruncher.library.extra.INPUT";
    private static final String EXTRA_OUTPUT_FILE = "com.badoo.hprof.cruncher.library.extra.INPUT";

    /**
     * Starts this service to check if there are any HPROF files to process.
     *
     * @see IntentService
     */
    public static void checkForDumps(@NonNull Context context) {
        Intent intent = new Intent(context, CruncherService.class);
        intent.setAction(ACTION_CHECK_FILES);
        context.startService(intent);
    }

    /**
     * Starts this service to crunch a HPROF file with the given parameters.
     *
     * @see IntentService
     */
    @SuppressWarnings("UnusedDeclaration")
    public static void crunchFile(@NonNull Context context, @NonNull String inputFile, @NonNull String outputFile) {
        Intent intent = new Intent(context, CruncherService.class);
        intent.setAction(ACTION_CRUNCH_FILE);
        intent.putExtra(EXTRA_INPUT_FILE, inputFile);
        intent.putExtra(EXTRA_OUTPUT_FILE, outputFile);
        context.startService(intent);
    }

    public CruncherService() {
        super("CruncherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CRUNCH_FILE.equals(action)) {
                final String inputFile = intent.getStringExtra(EXTRA_INPUT_FILE);
                final String outputFile = intent.getStringExtra(EXTRA_OUTPUT_FILE);
                crunchFile(inputFile, outputFile);
            }
            else if (ACTION_CHECK_FILES.equals(action)) {
                for (File file : getFilesDir().listFiles()) {
                    if (file.getName().endsWith(".hprof")) {
                        Log.d(TAG, "Found HPROF file: " + file + ", size: " + file.length());
                        String outFile = getFilesDir() + "/" + System.currentTimeMillis() + ".bmd.gz";
                        crunchFile(file.getAbsolutePath(), outFile);
                        if (file.delete()) {
                            Log.d(TAG, "Deleted " + file);
                        }
                    }
                    else if (file.getName().endsWith(".bmd") || file.getName().endsWith(".bmd.gz")) {
                        Log.d(TAG, "Found BMD file: " + file + ", size: " + file.length());
                    }
                }
            }
        }
    }

    private void crunchFile(String inputFile, String outputFile) {
        OutputStream out = null;
        try {
            out = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
            long startTime = SystemClock.elapsedRealtime();
            HprofCruncher.crunch(new File(inputFile), out);
            Log.d(TAG, "Crunching finished after " + (SystemClock.elapsedRealtime() - startTime) + "ms");
        } catch (IOException e) {
            Log.e(TAG, "Failed to crunch file", e);
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    // Ignored
                }
            }
        }
    }

}
