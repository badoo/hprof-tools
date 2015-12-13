package com.badoo.hprof.viewer.ui.classinfo;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.swing.SortOrder;

/**
 * Comparator for class names that also makes sure that the first row (containing query) is first after sorting.
 * Created by Erik Andre on 12/12/15.
 */
public class NameComparator implements Comparator<String> {

    private final String query;
    private final ClassesInfoPanel.InstanceTableRowSorter sorter;

    public NameComparator(@Nonnull ClassesInfoPanel.InstanceTableRowSorter sorter, @Nonnull String query) {
        this.sorter = sorter;
        this.query = query;
    }

    @Override
    public int compare(String lhs, String rhs) {
        if (query.equals(lhs)) {
            SortOrder order = sorter.getSortKeys().get(0).getSortOrder();
            return order == SortOrder.ASCENDING ? -1 : 1;
        }
        else if (query.equals(rhs)) {
            SortOrder order = sorter.getSortKeys().get(0).getSortOrder();
            return order == SortOrder.ASCENDING ? 1 : -1;
        }
        return lhs.compareTo(rhs);
    }
}
