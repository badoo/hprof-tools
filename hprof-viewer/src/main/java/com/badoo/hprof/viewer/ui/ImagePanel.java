package com.badoo.hprof.viewer.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Panel for displaying a single image
 * <p/>
 * Created by Erik Andre on 18/10/15.
 */
public class ImagePanel extends JPanel {

    private BufferedImage image;
    private Dimension size = new Dimension();

    public ImagePanel(BufferedImage image) {
        setImage(image);
    }

    public ImagePanel() {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) {
            return;
        }
        float xScale = getWidth() / (float) image.getWidth();
        float yScale = getHeight() / (float) image.getHeight();
        float scale = Math.min(xScale, yScale);
        g.drawImage(image, 0, 0, (int) (image.getWidth() * scale), (int) (image.getHeight() * scale), this);
    }

    public Dimension getPreferredSize() {
        return size;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        setComponentSize();
        repaint();
    }

    private void setComponentSize() {
        if (image != null) {
            size.width = image.getWidth();
            size.height = image.getHeight();
            revalidate();  // signal parent/scrollpane
        }
    }
}
