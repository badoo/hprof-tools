package com.badoo.hprof.viewer.ui.instances;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;

/**
 * Created by Erik Andre on 12/12/15.
 */
public class InstanceInfoTable extends JTable {

    private static final Color LIGHT_GRAY = new Color(0xeaeaea);

    public InstanceInfoTable() {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowSelectionAllowed(true);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer tableCellRenderer, int row, int col) {
        Component c = super.prepareRenderer(tableCellRenderer, row, col);
        if (row == 0) {
            c.setFont(c.getFont().deriveFont(Font.ITALIC));
        }
        c.setForeground(Color.BLACK);
        if (row % 2 == 1) {
            c.setBackground(LIGHT_GRAY);
        }
        else {
            c.setBackground(null);
        }
        return c;
    }
}
