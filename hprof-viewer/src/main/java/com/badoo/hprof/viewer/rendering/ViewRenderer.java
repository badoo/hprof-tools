package com.badoo.hprof.viewer.rendering;

import com.badoo.hprof.viewer.model.ImageView;
import com.badoo.hprof.viewer.model.TextView;
import com.badoo.hprof.viewer.model.View;
import com.badoo.hprof.viewer.model.ViewGroup;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class containing methods for rendering Views and ViewGroups
 * <p/>
 * Created by Erik Andre
 */
public class ViewRenderer {

    private static Stroke THIN = new BasicStroke(2);
    private static Stroke THICK = new BasicStroke(10);

    private Map<Integer, Color> colorMap = new HashMap<Integer, Color>();

    public BufferedImage renderViews(ViewGroup root) {
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

    private void renderViewGroup(ViewGroup view, Graphics2D canvas) {
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
            else if (child instanceof ImageView) {
                renderImageView((ImageView) child, canvas);
            }
            else {
                renderView(child, canvas);
            }
        }
        canvas.translate(-view.left, -view.top);
    }

    private void renderView(View view, Graphics2D canvas) {
        Color background = getViewBackgroundColor(view);
        if (background != null) {
            canvas.setColor(background);
            canvas.fillRect(view.left, view.top, view.getWidth(), view.getHeight());
        }
        canvas.setColor(getViewForegroundColor(view));
        canvas.setColor(view.isSelected() ? Color.RED : Color.BLACK);
        canvas.setStroke(view.isSelected() ? THICK : THIN);
        canvas.drawRect(view.left, view.top, view.getWidth(), view.getHeight());
    }

    private void renderTextView(TextView view, Graphics2D canvas) {
        renderView(view, canvas);
        // Seems like we have a problem here if the text is too long (rendering stalls)
        canvas.translate(view.left, view.top); // Apply translation
        canvas.drawString(view.text, 20, view.getHeight() / 2);
        canvas.translate(-view.left, -view.top); // Restore the translation
    }

    private void renderImageView(ImageView view, Graphics2D canvas) {
        renderView(view, canvas);
        canvas.translate(view.left, view.top); // Apply translation
        final BufferedImage image = view.getImage();
        if (image.getWidth() < view.getWidth() && image.getHeight() < view.getHeight()) { // Center inside
            int left = (view.getWidth() - image.getWidth()) / 2;
            int top = (view.getHeight() - image.getHeight()) / 2;
            canvas.drawImage(image, left, top, null);
        }
        else {
            canvas.drawImage(image, 0, 0, view.getWidth(), view.getHeight(), null); // Fit inside
        }
        canvas.translate(-view.left, -view.top); // Restore the translation
    }

    private Color getViewForegroundColor(View view) {
        if (view.isSelected()) {
            return Color.RED;
        }
        return view.getVisibility() == View.VISIBLE? Color.BLACK : Color.LIGHT_GRAY;
    }

    private Color getViewBackgroundColor(View view) {
        final int color = view.getBackgroundColor();
        if (color == 0) {
            return null;
        }
        if (!colorMap.containsKey(color)) {
            // Add some alpha to make it easier to see all layers
            int red = (color >> 16) & 0xff;
            int green = (color >> 8) & 0xff;
            int blue = color & 0xff;
            colorMap.put(color, new Color(red, green, blue, 120));
        }
        return colorMap.get(color);
    }
}
