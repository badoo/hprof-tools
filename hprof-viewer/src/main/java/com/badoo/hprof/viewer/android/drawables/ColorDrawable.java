package com.badoo.hprof.viewer.android.drawables;

import com.badoo.hprof.viewer.android.Drawable;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Drawable used to draw single colors
 *
 * Created by Erik Andre on 24/11/15.
 */
public class ColorDrawable implements Drawable {

    private final int baseArgbColor;
    private Color color;
    private float alpha = 1;

    public ColorDrawable(int argbColor) {
        this.baseArgbColor = argbColor;
        calculateColor();
    }

    @Override
    public void draw(Graphics2D canvas, int left, int top, int right, int bottom) {
        Color oldColor = canvas.getColor();
        canvas.setColor(color);
        int width = right - left;
        int height = bottom - top;
        canvas.fillRect(left, top, width, height);
        canvas.setColor(oldColor);
    }

    @Override
    public void setAlpha(int alpha) {
        float newAlpha = alpha / 255f;
        if (this.alpha != newAlpha) {
            this.alpha = newAlpha;
            calculateColor();
        }
    }

    private void calculateColor() {
        int baseAlpha = (baseArgbColor >> 24) & 0xff;
        float fAlpha = alpha * (baseAlpha / 255f);
        int red = (baseArgbColor >> 16) & 0xff;
        int green = (baseArgbColor >> 8) & 0xff;
        int blue = baseArgbColor & 0xff;
        this.color = new Color(red, green, blue, (int) (fAlpha * 255));
    }
}
