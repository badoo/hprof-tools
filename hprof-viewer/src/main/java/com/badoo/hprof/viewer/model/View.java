package com.badoo.hprof.viewer.model;

/**
 * Model class for an Android view
 */
public class View {

    public final int left;
    public final int right;
    public final int top;
    public final int bottom;

    public View(int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public int getWidth() {
        return right - left;
    }

    public int getHeight() {
        return bottom - top;
    }
}
