package com.badoo.hprof.viewer.ui.classinfo;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.swing.SortOrder;

/**
 * Comparator for class names that also makes sure that the first row (containing query) is first after sorting.
 * Created by Erik Andre on 12/12/15.
 */
public class NameComparator implements Comparator<Object> {

    private final String query;
    private final ClassesInfoPanel.ClassesTableRowSorter sorter;

    public NameComparator(@Nonnull ClassesInfoPanel.ClassesTableRowSorter sorter, @Nonnull String query) {
        this.sorter = sorter;
        this.query = query;
    }

    @Override
    public int compare(Object lhs, Object rhs) {
        if (lhs instanceof String) {
            SortOrder order = sorter.getSortKeys().get(0).getSortOrder();
            return order == SortOrder.ASCENDING ? -1 : 1;
        }
        else if (rhs instanceof String) {
            SortOrder order = sorter.getSortKeys().get(0).getSortOrder();
            return order == SortOrder.ASCENDING ? 1 : -1;
        }
        return ((ClassInfo) lhs).name.compareTo(((ClassInfo) rhs).name);
    }
}
