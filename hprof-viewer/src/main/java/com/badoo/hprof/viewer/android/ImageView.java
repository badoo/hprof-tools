package com.badoo.hprof.viewer.android;

/**
 * Model class representing the Android ImageView
 *
 * Created by Erik Andre on 22/11/15.
 */
public class ImageView extends View {

    private final Drawable image;

    public ImageView(String className, int left, int right, int top, int bottom, int flags, Drawable image) {
        super(className, left, right, top, bottom, flags);
        this.image = image;
    }

    public Drawable getImage() {
        return image;
    }
}
