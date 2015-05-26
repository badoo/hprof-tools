package com.badoo.hprof.validator.utils;

/**
 * Created by Erik Andre on 13/12/14.
 */
public class Log {

    public static void d(String tag, String msg) {
        System.out.println("[" + tag + "]: " + msg);
    }

    public static void d(String tag, String msg, Throwable t) {
        d(tag, msg);
        t.printStackTrace(System.out);
    }
}
