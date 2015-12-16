package com.badoo.hprof.viewer.android;

import java.awt.Graphics2D;

/**
 * Interface representing an Android Drawable
 *
 * Created by Erik Andre on 24/11/15.
 */
public interface Drawable {

    void draw(Graphics2D canvas, int left, int top, int right, int bottom);

    void setAlpha(int alpha);
}
