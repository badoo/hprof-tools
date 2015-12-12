package com.badoo.hprof.viewer.ui.instances;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.swing.SortOrder;

/**
 * Comparator for class names that also makes sure that the first row (containing query) is first after sorting.
 * Created by Erik Andre on 12/12/15.
 */
public class NameComparator implements Comparator<String> {

    private final String query;
    private final InstanceInfoPanel.InstanceTableRowSorter sorter;

    public NameComparator(@Nonnull InstanceInfoPanel.InstanceTableRowSorter sorter, @Nonnull String query) {
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
