package com.badoo.hprof.viewer.model;

/**
 * Model class for an Android view
 */
public class View {

    public static final int VISIBLE = 0x00000000;
    public static final int INVISIBLE = 0x00000004;
    public static final int GONE = 0x00000008;

    private final int VISIBILITY_MASK = 0x0000000C;

    public final int left;
    public final int right;
    public final int top;
    public final int bottom;
    private final String className;
    private final int flags;
    private boolean selected;
    private final int backgroundColor;

    public View(int left, int right, int top, int bottom, String className, int flags, int backgroundColor) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.className = className;
        this.flags = flags;
        this.backgroundColor = backgroundColor;
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

    public int getVisibility() {
        return flags & VISIBILITY_MASK;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public String toString() {
        return getClassName();
    }
}
