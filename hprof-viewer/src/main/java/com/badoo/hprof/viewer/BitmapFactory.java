package com.badoo.hprof.viewer;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for bitmaps (BufferedImage)
 *
 * Created by Erik Andre on 22/11/15.
 */
public class BitmapFactory {

    private static Map<Integer, BufferedImage> cache = new HashMap<Integer, BufferedImage>();

    public static BufferedImage createBitmap(int bitmapObjectId, byte[] data, int width, int height) {
        if (cache.containsKey(bitmapObjectId)) {
            return cache.get(bitmapObjectId);
        }
        if (data.length != width * height * 4) {
            throw new IllegalArgumentException("Invalid bitmap dimensions! length=" + data.length + ", w=" + width + ", h=" + height);
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < data.length; i += 4) {
            int pixel = i / 4;
            // Force the alpha to make the image more visible
            int value = ((data[i + 3] << 24) & 0xff000000) |  ((data[i] << 16) & 0xff0000) | ((data[i + 1] << 8) & 0xff00) | (data[i + 2] & 0xff);
            image.setRGB(pixel % width, pixel / width, value);
        }
        cache.put(bitmapObjectId, image);
        return image;
    }

}
