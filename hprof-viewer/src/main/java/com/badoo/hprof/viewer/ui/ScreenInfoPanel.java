package com.badoo.hprof.viewer.ui;

import com.badoo.hprof.viewer.android.Activity;
import com.badoo.hprof.viewer.android.Bundle;
import com.badoo.hprof.viewer.android.Intent;
import com.badoo.hprof.viewer.android.View;
import com.badoo.hprof.viewer.android.ViewGroup;
import com.badoo.hprof.viewer.factory.SystemInfo;
import com.badoo.hprof.viewer.rendering.ViewRenderer;
import com.badoo.hprof.viewer.factory.Screen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Nonnull;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * Panel containing information about Views and activities.
 * <p/>
 * Created by Erik Andre on 22/11/15.
 */
public class ScreenInfoPanel extends JPanel implements TreeSelectionListener, ItemListener {

    private static final String[] HEADER = new String[]{"Name", "Value"};

    private final JTree viewTree;
    private final ViewRenderer renderer = new ViewRenderer();
    private final JComboBox rootPicker;
    private final JCheckBox showBoundsBox;
    private final JCheckBox forceAlpha;
    private final JTable infoTable;
    private Screen selectedScreen;
    private ImagePanel imagePanel;
    private View selectedView;

    public ScreenInfoPanel(@Nonnull List<Screen> screens) {
        super(new BorderLayout());
        imagePanel = new ImagePanel();
        viewTree = new JTree(new DefaultMutableTreeNode("Loading..."));
        viewTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        viewTree.addTreeSelectionListener(this);
        JScrollPane treeScroller = new JScrollPane(viewTree);

        rootPicker = new JComboBox(new Vector<Object>(screens));
        rootPicker.addItemListener(this);

        JPanel settingsPanel = new JPanel(new GridLayout(1, 2));
        showBoundsBox = new JCheckBox("Show layout bounds", true);
        showBoundsBox.addItemListener(this);
        forceAlpha = new JCheckBox("Force alpha", true);
        forceAlpha.addItemListener(this);
        settingsPanel.add(showBoundsBox);
        settingsPanel.add(forceAlpha);
        settingsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        infoTable = new JTable();
        infoTable.setRowSelectionAllowed(false);
        infoTable.setColumnSelectionAllowed(false);
        infoTable.setCellSelectionEnabled(false);
        infoTable.setShowGrid(true);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomPanel.add(infoTable, BorderLayout.CENTER);
        bottomPanel.add(infoTable.getTableHeader(), BorderLayout.NORTH);
        bottomPanel.add(settingsPanel, BorderLayout.SOUTH);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(rootPicker, BorderLayout.NORTH);
        leftPanel.add(treeScroller, BorderLayout.CENTER);
        leftPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Split pane for the tree and image views
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(imagePanel);
        add(splitPane, BorderLayout.CENTER);
        selectedScreen = screens.get(0);
        update();
    }

    private void update() {
        showViewTree();
        updateImage(true);
        updateInfoTable();
    }

    private void updateInfoTable() {
        Activity activity = selectedScreen.getActivity();
        Object cells[][];
        if (activity != null && activity.getIntent() != null) {
            Intent intent = activity.getIntent();
            boolean hasAction = intent.getAction() != null;
            Map<Object, Object> params = intent.getExtras().getMap();
            cells = new Object[params.size() + (hasAction ? 1 : 0)][2];
            if (hasAction) {
                cells[0][0] = "Action";
                cells[0][1] = intent.getAction();
            }
            int position = hasAction ? 1 : 0;
            for (Map.Entry<Object, Object> entry : params.entrySet()) {
                cells[position][0] = entry.getKey();
                cells[position][1] = entry.getValue();
                position++;
            }
        }
        else {
            cells = new String[][]{{"No intent data", ""}};
        }
        infoTable.setModel(new DefaultTableModel(cells, HEADER));
    }

    public void showViewTree() {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(selectedScreen.getViewRoot());
        addChildViews(rootNode, selectedScreen.getViewRoot());
        DefaultTreeModel model = new DefaultTreeModel(rootNode);
        viewTree.setModel(model);
    }

    private void addChildViews(DefaultMutableTreeNode parent, ViewGroup group) {
        for (View view : group.getChildren()) {
            if (view instanceof ViewGroup) {
                DefaultMutableTreeNode newParent = new DefaultMutableTreeNode(view);
                parent.add(newParent);
                addChildViews(newParent, (ViewGroup) view);
            }
            else {
                parent.add(new DefaultMutableTreeNode(view));
            }
        }
    }

    public void updateImage(boolean resize) {
        BufferedImage image = renderer.renderViews(selectedScreen.getViewRoot());
        imagePanel.setImage(image);
        if (resize) {
            Dimension size = new Dimension((int) (imagePanel.getPreferredSize().getWidth() + rootPicker.getPreferredSize().getWidth() + 15), (int) imagePanel.getPreferredSize().getHeight() + 25);
            setPreferredSize(size);
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent event) {
        if (selectedView != null) {
            selectedView.setSelected(false);
        }
        if (event.getNewLeadSelectionPath() == null) {
            selectedView = null;
            return;
        }
        DefaultMutableTreeNode newNode = (DefaultMutableTreeNode) event.getNewLeadSelectionPath().getLastPathComponent();
        View newView = (View) newNode.getUserObject();
        newView.setSelected(true);
        selectedView = newView;
        updateImage(false);
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getSource() == rootPicker) {
            selectedScreen = ((Screen) itemEvent.getItem());
            update();
        }
        else if (itemEvent.getSource() == showBoundsBox) {
            renderer.setShowBounds(showBoundsBox.isSelected());
            updateImage(false);
        }
        else if (itemEvent.getSource() == forceAlpha) {
            renderer.setForceBackgroundTransparency(forceAlpha.isSelected());
            updateImage(false);
        }
    }
}
