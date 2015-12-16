package com.badoo.hprof.viewer.android.drawables;

import com.badoo.hprof.viewer.android.Drawable;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Drawable used to draw Bitmaps (BufferedImage)
 *
 * Created by Erik Andre on 24/11/15.
 */
public class BitmapDrawable implements Drawable {

    private final BufferedImage bitmap;

    public BitmapDrawable(BufferedImage bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public void draw(Graphics2D canvas, int left, int top, int right, int bottom) {
        int boundWidth = right - left;
        int boundHeight = bottom - top;
        if (bitmap.getWidth() < boundWidth && bitmap.getHeight() < boundHeight) { // Center inside
            int adjustedLeft = left + (boundWidth - bitmap.getWidth()) / 2;
            int adjustedTop = top + (boundHeight - bitmap.getHeight()) / 2;
            canvas.drawImage(bitmap, adjustedLeft, adjustedTop, null);
        }
        else {
            canvas.drawImage(bitmap, left, top, boundWidth, boundHeight, null); // Fit inside
        }
    }

    @Override
    public void setAlpha(int alpha) {
        // TODO
    }
}
