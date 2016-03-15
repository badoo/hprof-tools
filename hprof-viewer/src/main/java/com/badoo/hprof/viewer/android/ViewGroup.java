package com.badoo.hprof.viewer.android;

import com.badoo.hprof.library.model.Instance;

import java.util.List;

import javax.annotation.Nonnull;

/**
 */
public class ViewGroup extends View {

    private final List<View> children;

    public ViewGroup(@Nonnull Instance instance, @Nonnull String className, @Nonnull List<View> children, int left, int right, int top, int bottom, int flags) {
        super(instance, className, left, right, top, bottom, flags);
        this.children = children;
    }

    public List<View> getChildren() {
        return children;
    }
}
