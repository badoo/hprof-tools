package com.badoo.hprof.viewer.android.drawables;

import com.badoo.hprof.viewer.android.Drawable;

import java.awt.Graphics2D;

/**
 * Dummpy drawable which draws nothing
 *
 * Created by Erik Andre on 24/11/15.
 */
public class EmptyDrawable implements Drawable {

    @Override
    public void draw(Graphics2D canvas, int left, int top, int right, int bottom) {
        // Not used
    }

    @Override
    public void setAlpha(int alpha) {
        // Not used
    }
}
