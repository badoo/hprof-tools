package com.badoo.hprof.viewer.rendering;

import com.badoo.hprof.viewer.model.TextView;
import com.badoo.hprof.viewer.model.View;
import com.badoo.hprof.viewer.model.ViewGroup;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Utility class containing methods for rendering Views and ViewGroups
 *
 * Created by Erik Andre
 */
public class ViewRenderer {

    public static BufferedImage renderViews(ViewGroup root) {
        if (root.getWidth() == 0 || root.getHeight() == 0) {
            throw new IllegalArgumentException("View root has no size!");
        }
        BufferedImage buffer = new BufferedImage(root.getWidth() / 2 + 2, root.getHeight() / 2 + 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D canvas = (Graphics2D) buffer.getGraphics();
        canvas.scale(0.5, 0.5);
        canvas.setFont(canvas.getFont().deriveFont(24f));
        renderViewGroup(root, canvas);
        return buffer;
    }

    private static void renderViewGroup(ViewGroup view, Graphics2D canvas) {
        renderView(view, canvas); // Draw the outline
        canvas.translate(view.left, view.top);
        for (View child : view.getChildren()) {
            if (child instanceof ViewGroup) {
                renderViewGroup((ViewGroup) child, canvas);
            }
            else if (child instanceof TextView) {
                renderTextView((TextView) child, canvas);
            }
            else {
                renderView(child, canvas);
            }
        }
        canvas.translate(-view.left, -view.top);
    }

    private static void renderView(View view, Graphics2D canvas) {
        canvas.setColor(Color.BLACK);
        canvas.drawRect(view.left, view.top, view.getWidth(), view.getHeight());
    }

    private static void renderTextView(TextView view, Graphics2D canvas) {
        canvas.setColor(Color.RED);
        canvas.drawRect(view.left, view.top, view.getWidth(), view.getHeight());
        // Seems like we have a problem here if the text is too long (rendering stalls)
//        canvas.translate(view.left, view.top); // Apply translation
//        canvas.drawString(view.text, 20, view.getHeight() / 2);
//        canvas.translate(-view.left, -view.top); // Restore the translation
    }
}
