package com.badoo.hprof.viewer.android;

import com.badoo.hprof.library.model.Instance;

import javax.annotation.Nonnull;

/**
 * Model class representing the Android TextView
 */
public class TextView extends View {

    private final CharSequence text;

    public TextView(@Nonnull Instance instance, @Nonnull String className, int left, int right, int top, int bottom, int flags, CharSequence text) {
        super(instance, className, left, right, top, bottom, flags);
        this.text = text;
    }

    public CharSequence getText() {
        return text;
    }
}
