package com.badoo.hprof.viewer.ui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * Created by Erik Andre on 22/11/15.
 */
public class MainWindow extends JFrame {

    private final JSplitPane splitPane;
    private ImagePanel imagePanel;
    private JTree viewTree;

    public MainWindow() {
        super("Hprof Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        imagePanel = new ImagePanel();

        DefaultMutableTreeNode top =
            new DefaultMutableTreeNode("View tree goes here!");
        viewTree = new JTree(top);
        viewTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        JScrollPane treeScroller = new JScrollPane(viewTree);

        // Split pane for the tree and image views
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(treeScroller);
        splitPane.setRightComponent(imagePanel);
        splitPane.setDividerLocation(300);
        splitPane.setPreferredSize(new Dimension(800, 700));
        add(splitPane);
        setVisible(true);
    }

    public void updateImage(BufferedImage image) {
        imagePanel.setImage(image);
        Dimension size = new Dimension((int) (imagePanel.getPreferredSize().getWidth() + 310), (int) imagePanel.getPreferredSize().getHeight());
        splitPane.setPreferredSize(size);
        pack();
    }
}
