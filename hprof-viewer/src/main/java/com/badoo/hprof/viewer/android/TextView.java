package com.badoo.hprof.viewer.android;

/**
 * Model class representing the Android TextView
 */
public class TextView extends View {

    public final String text;

    public TextView(String className, int left, int right, int top, int bottom, int flags, String text) {
        super(className, left, right, top, bottom, flags);
        this.text = text;
    }
}
