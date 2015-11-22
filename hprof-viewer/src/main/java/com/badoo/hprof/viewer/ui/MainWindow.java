package com.badoo.hprof.viewer.ui;

import com.badoo.hprof.viewer.model.View;
import com.badoo.hprof.viewer.model.ViewGroup;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * Created by Erik Andre on 22/11/15.
 */
public class MainWindow extends JFrame {

    private final JSplitPane splitPane;
    private final JTree viewTree;
    private ImagePanel imagePanel;

    public MainWindow(List<ViewGroup> roots) {
        super("Hprof Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        imagePanel = new ImagePanel();

        viewTree = new JTree(new DefaultMutableTreeNode("Loading..."));
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
        showViewTree(roots.get(0));
    }

    public void showViewTree(ViewGroup root) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");
        addChildViews(rootNode, root);
        DefaultTreeModel model = new DefaultTreeModel(rootNode);
        viewTree.setModel(model);
    }

    private void addChildViews(DefaultMutableTreeNode parent, ViewGroup group) {
        for (View view : group.getChildren()) {
            if (view instanceof  ViewGroup) {
                DefaultMutableTreeNode newParent = new DefaultMutableTreeNode("ViewGroup");
                parent.add(newParent);
                addChildViews(newParent, (ViewGroup) view);
            }
            else {
                parent.add(new DefaultMutableTreeNode("View"));
            }
        }
    }

    public void updateImage(BufferedImage image) {
        imagePanel.setImage(image);
        Dimension size = new Dimension((int) (imagePanel.getPreferredSize().getWidth() + 310), (int) imagePanel.getPreferredSize().getHeight());
        splitPane.setPreferredSize(size);
        pack();
    }
}
