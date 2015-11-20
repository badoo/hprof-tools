package com.badoo.hprof.viewer.model;

import java.util.List;

/**
 */
public class ViewGroup extends View {

    private final List<View> children;

    public ViewGroup(List<View> children, int left, int right, int top, int bottom) {
        super(left, right, top, bottom);
        this.children = children;
    }

    public List<View> getChildren() {
        return children;
    }
}
