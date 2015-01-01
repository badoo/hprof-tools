package com.badoo.hprof.cruncher.library;

import android.content.Context;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.util.Log;

import static java.lang.Thread.UncaughtExceptionHandler;

public class HprofCatcher {

    private static final String TAG = "HprofCatcher";
    private static boolean sHandlerInstalled;

    /**
     * Initialize HprofCatcher so that it will handle any uncaught OutOfMemoryErrors.
     *
     * @param context Context used to process memory dumps.
     */
    public static void init(@NonNull Context context) {
        if (sHandlerInstalled) {
            return;
        }
        UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new MemoryDumpHandler(context.getApplicationContext(), oldHandler));
        sHandlerInstalled = true;
        // Check if there are any hprof files to process
        CruncherService.checkForDumps(context);
    }

    private static class MemoryDumpHandler implements UncaughtExceptionHandler {

        private final UncaughtExceptionHandler mOldHandler;
        private final Context mContext;

        private MemoryDumpHandler(@NonNull Context context, @NonNull UncaughtExceptionHandler oldHandler) {
            mOldHandler = oldHandler;
            mContext = context;
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            if (ex instanceof OutOfMemoryError) {
                try {
                    String fileName = mContext.getFilesDir().getAbsolutePath() + "/" + System.currentTimeMillis() + ".hprof";
                    Log.d(TAG, "Writing memory dump to: " + fileName);
                    Debug.dumpHprofData(fileName);
                }
                catch (Throwable t) {
                    // Make sure we don't throw any new exception here!
                    Log.e(TAG, "Failed to write memory dump", t);
                }
            }
            mOldHandler.uncaughtException(thread, ex);
        }
    }

}
