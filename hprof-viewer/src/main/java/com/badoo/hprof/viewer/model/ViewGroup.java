package com.badoo.hprof.viewer.model;

import java.util.List;

/**
 */
public class ViewGroup extends View {

    private final List<View> children;

    public ViewGroup(List<View> children, int left, int right, int top, int bottom, String className) {
        super(left, right, top, bottom, className);
        this.children = children;
    }

    public List<View> getChildren() {
        return children;
    }
}
