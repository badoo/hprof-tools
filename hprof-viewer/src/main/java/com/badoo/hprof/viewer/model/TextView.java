package com.badoo.hprof.viewer.model;

/**
 */
public class TextView extends View {

    public final String text;

    public TextView(String text, int left, int right, int top, int bottom) {
        super(left, right, top, bottom, "TextView");
        this.text = text;
    }
}
