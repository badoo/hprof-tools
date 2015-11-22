package com.badoo.hprof.viewer.model;

/**
 * Model class for an Android view
 */
public class View {

    public final int left;
    public final int right;
    public final int top;
    public final int bottom;
    private final String className;
    private boolean selected;

    public View(int left, int right, int top, int bottom, String className) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.className = className;
    }

    public int getWidth() {
        return right - left;
    }

    public int getHeight() {
        return bottom - top;
    }

    public String getClassName() {
        return className;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return getClassName();
    }
}
