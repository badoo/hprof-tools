package com.badoo.hprof.viewer.android;

import com.badoo.hprof.library.model.Instance;

import javax.annotation.Nonnull;

/**
 * Model class representing the Android ImageView
 * <p/>
 * Created by Erik Andre on 22/11/15.
 */
public class ImageView extends View {

    private final Drawable image;

    public ImageView(@Nonnull Instance instance, @Nonnull String className, int left, int right, int top, int bottom, int flags, Drawable image) {
        super(instance, className, left, right, top, bottom, flags);
        this.image = image;
    }

    public Drawable getImage() {
        return image;
    }
}
