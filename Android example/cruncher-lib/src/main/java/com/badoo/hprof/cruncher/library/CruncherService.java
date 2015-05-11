package com.badoo.hprof.cruncher.library;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.badoo.hprof.cruncher.HprofCruncher;
import com.badoo.hprof.cruncher.HprofFileSource;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import static com.badoo.hprof.cruncher.HprofCruncher.Config;

/**
 * A Service that processes HPROF files, turning them into BMD files. Since converting from HPROF to BMD can be quite a resource intensive operation it is recommended
 * that you only do so when it will not impact the user. For example it would make sense to wait for the device to be plugged into a charger and idling before starting the service.
 * <p/>
 * In the current implementation there is no way to stop crunching once it has started so chose the time carefully!
 */
@SuppressWarnings({"PointlessBooleanExpression", "ConstantConditions"})
public class CruncherService extends IntentService {

    // Actions and extras used when broadcasting status updates through local broadcasts
    public static final String ACTION_CRUNCHING_STARTED = CruncherService.class.getPackage().getName() + ".action.CRUNCH_STARTED";
    public static final String ACTION_CRUNCHING_FINISHED = CruncherService.class.getPackage().getName() + ".action.CRUNCH_FINISHED";

    public static final String EXTRA_SUCCESS = CruncherService.class.getPackage().getName() + ".extra.SUCCESS";
    public static final String EXTRA_HPROF_FILE = CruncherService.class.getPackage().getName() + ".extra.HPROF_FILE";
    public static final String EXTRA_BMD_FILE = CruncherService.class.getPackage().getName() + ".extra.BMD_FILE";
    public static final String EXTRA_HPROF_SIZE = CruncherService.class.getPackage().getName() + ".extra.HPROF_SIZE";
    public static final String EXTRA_BMD_SIZE = CruncherService.class.getPackage().getName() + ".extra.BMD_SIZE";

    private static final String TAG = CruncherService.class.getSimpleName();
    private static final String ACTION_CRUNCH_FILE = CruncherService.class.getName() + ".CRUNCH_FILE";
    private static final String ACTION_CHECK_FILES = CruncherService.class.getName() + ".CHECK_FILES";

    private static final boolean DEBUG = true;
    private static final long TIME_LIMIT = 5 * 60 * 1000;

    /**
     * Starts this service to check if there are any HPROF files to process and if any are found,
     * start converting them to BMD.
     *
     * @see IntentService
     */
    public static void processHprofDumps(@NonNull Context context) {
        Intent intent = new Intent(context, CruncherService.class);
        intent.setAction(ACTION_CHECK_FILES);
        context.startService(intent);
    }

    public CruncherService() {
        super("CruncherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // The service handles two actions, checking for files on disk and crunching them (converting to BMD).
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CRUNCH_FILE.equals(action)) {
                final String inputFile = intent.getStringExtra(EXTRA_HPROF_FILE);
                final String outputFile = intent.getStringExtra(EXTRA_BMD_FILE);
                crunchFile(inputFile, outputFile);
            }
            else if (ACTION_CHECK_FILES.equals(action)) {
                for (File file : getFilesDir().listFiles()) {
                    if (file.getName().endsWith(".hprof")) {
                        if (DEBUG) {
                            Log.d(TAG, "Found HPROF file: " + file + ", size: " + file.length());
                        }
                        String outFile = getFilesDir() + "/" + System.currentTimeMillis() + ".bmd.gz";
                        crunchFile(file.getAbsolutePath(), outFile);
                        final boolean deleted = file.delete();
                        if (DEBUG) {
                            if (deleted) {
                                Log.d(TAG, "Deleted " + file);
                            }
                            else {
                                Log.d(TAG, "Failed to delete " + file);
                            }
                        }
                    }
                    else if (DEBUG && file.getName().endsWith(".bmd") || file.getName().endsWith(".bmd.gz")) {
                        Log.d(TAG, "Found BMD file: " + file + ", size: " + file.length());
                    }
                }
            }
        }
    }

    private void crunchFile(String inputFilePath, String outputFilePath) {
        final File inputFile = new File(inputFilePath);
        Intent startIntent = new Intent(ACTION_CRUNCHING_STARTED);
        startIntent.putExtra(EXTRA_HPROF_FILE, inputFilePath);
        startIntent.putExtra(EXTRA_BMD_FILE, outputFilePath);
        startIntent.putExtra(EXTRA_HPROF_SIZE, inputFile.length());
        LocalBroadcastManager.getInstance(this).sendBroadcast(startIntent);
        OutputStream out = null;
        boolean success = false;
        try {
            out = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(outputFilePath)));
            long startTime = SystemClock.elapsedRealtime();
            Config config = new Config();
            config.enableStats(true).setTimeLimit(TIME_LIMIT);
            HprofCruncher.crunch(new HprofFileSource(inputFile), out, config);
            if (DEBUG) {
                Log.d(TAG, "Crunching finished after " + (SystemClock.elapsedRealtime() - startTime) + "ms");
            }
            success = true;
        }
        catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, "Failed to crunch file", e);
            }
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
        Intent finishIntent = new Intent(ACTION_CRUNCHING_FINISHED);
        finishIntent.putExtra(EXTRA_SUCCESS, success);
        finishIntent.putExtra(EXTRA_HPROF_FILE, inputFilePath);
        finishIntent.putExtra(EXTRA_BMD_FILE, outputFilePath);
        startIntent.putExtra(EXTRA_HPROF_SIZE, inputFile.length());
        if (success) {
            startIntent.putExtra(EXTRA_BMD_SIZE, new File(outputFilePath).length());
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(finishIntent);
    }

}
