package com.badoo.hprof.viewer.android;

/**
 * Model class for an Android view
 */
public class View {

    public static final int VISIBLE = 0x00000000;
    public static final int INVISIBLE = 0x00000004;
    public static final int GONE = 0x00000008;

    public final int left;
    public final int right;
    public final int top;
    public final int bottom;

    private final int VISIBILITY_MASK = 0x0000000C;
    private final String className;
    private final int flags;
    private boolean selected;
    private Drawable background;

    public View(String className, int left, int right, int top, int bottom, int flags) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.className = className;
        this.flags = flags;
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

    public Drawable getBackground() {
        return background;
    }

    public void setBackground(Drawable background) {
        this.background = background;
    }

    @Override
    public String toString() {
        return getClassName();
    }
}
