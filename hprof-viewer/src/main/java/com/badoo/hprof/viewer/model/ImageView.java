package com.badoo.hprof.viewer.model;

import java.awt.image.BufferedImage;

/**
 * Created by Erik Andre on 22/11/15.
 */
public class ImageView extends View {

    private final BufferedImage image;

    public ImageView(int left, int right, int top, int bottom, String className, int flags, int backgroundColor, BufferedImage image) {
        super(left, right, top, bottom, className, flags, backgroundColor);
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }
}
