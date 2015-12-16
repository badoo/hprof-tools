package com.badoo.hprof.viewer.android;

import java.util.List;

/**
 */
public class ViewGroup extends View {

    private final List<View> children;

    public ViewGroup(String className, List<View> children, int left, int right, int top, int bottom, int flags) {
        super(className, left, right, top, bottom, flags);
        this.children = children;
    }

    public List<View> getChildren() {
        return children;
    }
}
