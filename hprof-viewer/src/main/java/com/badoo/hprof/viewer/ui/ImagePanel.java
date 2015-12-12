package com.badoo.hprof.viewer.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Panel for displaying a single image
 *
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
        g.drawImage(image, 0, 0, this);
//        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
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
        if(image != null) {
            size.width  = image.getWidth();
            size.height = image.getHeight();
            revalidate();  // signal parent/scrollpane
        }
    }
}
