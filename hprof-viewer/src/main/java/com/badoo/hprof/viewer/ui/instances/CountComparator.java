package com.badoo.hprof.viewer.ui.instances;

import java.util.Comparator;

import javax.annotation.Nonnull;
import javax.swing.SortOrder;

/**
 * Comparator for instance counts that also makes sure that the first row (containing query) is first after sorting.
 * Created by Erik Andre on 12/12/15.
 */
public class CountComparator implements Comparator<Object> {

    private final InstanceInfoPanel.InstanceTableRowSorter sorter;

    public CountComparator(@Nonnull InstanceInfoPanel.InstanceTableRowSorter sorter) {
        this.sorter = sorter;
    }

    @Override
    public int compare(Object lhs, Object rhs) {
        if (!(lhs instanceof Integer)) {
            SortOrder order = sorter.getSortKeys().get(0).getSortOrder();
            return order == SortOrder.ASCENDING ? -1 : 1;
        }
        else if (!(rhs instanceof Integer)) {
            SortOrder order = sorter.getSortKeys().get(0).getSortOrder();
            return order == SortOrder.ASCENDING ? 1 : -1;
        }
        return ((Integer) lhs).compareTo((Integer) rhs);
    }
}
