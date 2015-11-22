package com.badoo.hprof.viewer.rendering;

import com.badoo.hprof.viewer.model.TextView;
import com.badoo.hprof.viewer.model.View;
import com.badoo.hprof.viewer.model.ViewGroup;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

/**
 * Utility class containing methods for rendering Views and ViewGroups
 * <p/>
 * Created by Erik Andre
 */
public class ViewRenderer {

    private static Stroke THIN = new BasicStroke(2);
    private static Stroke THICK = new BasicStroke(10);

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
            if (child.getVisibility() == View.GONE) {
                continue; // Skip GONE views by default
            }
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
        canvas.setColor(getViewColor(view));
        canvas.setColor(view.isSelected() ? Color.RED : Color.BLACK);
        canvas.setStroke(view.isSelected() ? THICK : THIN);
        canvas.drawRect(view.left, view.top, view.getWidth(), view.getHeight());
    }

    private static void renderTextView(TextView view, Graphics2D canvas) {
        canvas.setColor(getViewColor(view));
        canvas.setStroke(view.isSelected() ? THICK : THIN);
        canvas.drawRect(view.left, view.top, view.getWidth(), view.getHeight());
        // Seems like we have a problem here if the text is too long (rendering stalls)
        canvas.translate(view.left, view.top); // Apply translation
        String text = view.text.length() < 20 ? view.text : view.text.substring(0, 20);
        canvas.drawString(text, 20, view.getHeight() / 2);
        canvas.translate(-view.left, -view.top); // Restore the translation
    }

    private static Color getViewColor(View view) {
        if (view.isSelected()) {
            return Color.RED;
        }
        return view.getVisibility() == View.VISIBLE? Color.BLACK : Color.LIGHT_GRAY;
    }
}
