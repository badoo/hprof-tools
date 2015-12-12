package com.badoo.hprof.viewer.rendering;

import com.badoo.hprof.viewer.android.Drawable;
import com.badoo.hprof.viewer.android.ImageView;
import com.badoo.hprof.viewer.android.TextView;
import com.badoo.hprof.viewer.android.View;
import com.badoo.hprof.viewer.android.ViewGroup;
import com.badoo.hprof.viewer.factory.SystemInfo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.annotation.Nonnull;

/**
 * Utility class containing methods for rendering Views and ViewGroups
 * <p/>
 * Created by Erik Andre
 */
@SuppressWarnings("unused")
public class ViewRenderer {

    private static Stroke THIN_LINE = new BasicStroke(2);
    private static Stroke THICK_LINE = new BasicStroke(10);
    private static final float SCALE_FACTOR = 0.5f;

    private boolean showBounds = true;
    private boolean renderText = true;
    private boolean renderBackgrounds = true;
    private boolean renderImageViews = true;
    private boolean forceAlpha = true;

    /**
     * Render all Views in a ViewGroup
     *
     * @param root the root of the ViewGroup to render
     * @return the rendered image
     */
    public BufferedImage renderViews(@Nonnull ViewGroup root) {
        final long start = System.currentTimeMillis();
        if (root.getWidth() == 0 || root.getHeight() == 0) {
            throw new IllegalArgumentException("View root has no size!");
        }
        int height = getMaxHeight(root);
        int width = getMaxWidth(root);
        BufferedImage buffer = new BufferedImage((int) (width * SCALE_FACTOR), (int) (height * SCALE_FACTOR), BufferedImage.TYPE_INT_ARGB);
        Graphics2D canvas = (Graphics2D) buffer.getGraphics();
        canvas.scale(SCALE_FACTOR, SCALE_FACTOR);
        canvas.setFont(canvas.getFont().deriveFont(24f));
        renderViewGroup(root, canvas);
        System.out.println("Rendered views in " + (System.currentTimeMillis() - start) + "ms");
        return buffer;
    }

    private int getMaxHeight(ViewGroup root) {
        int height = root.getHeight();
        for (View view : root.getChildren()) {
            if (view instanceof ViewGroup) {
                int childHeight = getMaxHeight((ViewGroup) view);
                if (view.top + childHeight > height) {
                    height = view.top + childHeight;
                }
            }
            else {
                int childHeight = view.getHeight();
                if (view.top + childHeight > height) {
                    height = view.top + childHeight;
                }
            }
        }
        return height;
    }

    private int getMaxWidth(ViewGroup root) {
        int width = root.getHeight();
        for (View view : root.getChildren()) {
            if (view instanceof ViewGroup) {
                int childWidth = getMaxWidth((ViewGroup) view);
                if (view.left + childWidth > width) {
                    width = view.left + childWidth;
                }
            }
            else {
                int childWidth = view.getWidth();
                if (view.left + childWidth > width) {
                    width = view.left + childWidth;
                }
            }
        }
        return width;
    }

    /**
     * Enable or disable rendering of View bounds.
     *
     * @param showBounds true if bounds should be rendered
     */
    public void setShowBounds(boolean showBounds) {
        this.showBounds = showBounds;
    }

    /**
     * Enable or disable forced background transparency.
     *
     * @param forceAlpha true if View backgrounds should be forced to be semi transparent
     */
    public void setForceBackgroundTransparency(boolean forceAlpha) {
        this.forceAlpha = forceAlpha;
    }

    /**
     * Enable or disable rendering of text
     *
     * @param renderText true if texts should be rendered
     */
    public void setRenderText(boolean renderText) {
        this.renderText = renderText;
    }

    /**
     * Enable or disable rendering of View background drawables
     *
     * @param renderBackgrounds true if backgrounds should be rendered
     */
    public void setRenderBackgrounds(boolean renderBackgrounds) {
        this.renderBackgrounds = renderBackgrounds;
    }


    /**
     * Enable or disable rendering of images (in ImageViews)
     *
     * @param renderImageViews true if images should be rendered
     */
    public void setRenderImages(boolean renderImageViews) {
        this.renderImageViews = renderImageViews;
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
        Drawable background = view.getBackground();
        if (renderBackgrounds && background != null) {
            if (forceAlpha) {
                background.setAlpha(120);
            }
            else {
                background.setAlpha(255);
            }
            background.draw(canvas, view.left, view.top, view.right, view.bottom);
        }
        canvas.setColor(getViewOutlineColor(view));
        canvas.setColor(view.isSelected() ? Color.RED : Color.BLACK);
        canvas.setStroke(view.isSelected() ? THICK_LINE : THIN_LINE);
        if (showBounds) {
            canvas.drawRect(view.left, view.top, view.getWidth(), view.getHeight());
        }
    }

    private void renderTextView(TextView view, Graphics2D canvas) {
        renderView(view, canvas);
        // Seems like we have a problem here if the text is too long (rendering stalls)
        canvas.translate(view.left, view.top); // Apply translation
        CharSequence text = view.getText();
        if (renderText && text != null) {
            canvas.drawString(text.toString(), 20, view.getHeight() / 2);
        }
        canvas.translate(-view.left, -view.top); // Restore the translation
    }

    private void renderImageView(ImageView view, Graphics2D canvas) {
        renderView(view, canvas);
        Drawable image = view.getImage();
        if (renderImageViews && image != null) {
            image.draw(canvas, view.left, view.top, view.right, view.bottom);
        }
    }

    private Color getViewOutlineColor(View view) {
        if (view.isSelected()) {
            return Color.RED;
        }
        return view.getVisibility() == View.VISIBLE ? Color.BLACK : Color.LIGHT_GRAY;
    }

//    // Returns the largest bounding box that would fit all views in the hierarchy (including those
//    // that are positioned outside of their parents)
//    private Rectangle getMaximumBounds(@Nonnull ViewGroup root) {
//        Rectangle bounds = new Rectangle(root.left, root.top, root.getWidth(), root.getHeight());
//        for (View view : root.getChildren()) {
//            Rectangle childBounds = getMaximumBounds(view);
//            childBounds.translate(bounds.x, bounds.y);
//            if (!bounds.contains(childBounds)) {
//                bounds = bounds.union(childBounds);
//            }
//        }
//        return bounds;
//    }
//
//    private Rectangle getMaximumBounds(View view) {
//        return new Rectangle(view.left, view.top, view.getWidth(), view.getHeight());
//    }

}
