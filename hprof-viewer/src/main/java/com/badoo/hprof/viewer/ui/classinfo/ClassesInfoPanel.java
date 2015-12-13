package com.badoo.hprof.viewer.ui.classinfo;

import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.provider.ClassProvider;
import com.badoo.hprof.viewer.provider.InstanceProvider;

import java.awt.BorderLayout;
import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Panel showing information about classinfo and classes found in the memory dump.
 * <p/>
 * Created by Erik Andre on 12/12/15.
 */
public class ClassesInfoPanel extends JPanel implements ClassesInfoPresenter.View {

    static class InstanceTableModel extends DefaultTableModel {

        public InstanceTableModel(Object[][] cells) {
            super(cells, HEADER);
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return row == 0;
        }
    }

    static class InstanceTableRowSorter extends TableRowSorter<InstanceTableModel> {

        public InstanceTableRowSorter(@Nonnull final InstanceTableModel model, @Nonnull String query) {
            super(model);
            setComparator(0, new NameComparator(this, query));
            setComparator(1, new CountComparator(this));
            setComparator(2, new CountComparator(this));
        }

        @Override
        public void toggleSortOrder(int i) {
            super.toggleSortOrder(i);
        }
    }

    private static final String[] HEADER = {"Name", "Instances", "Shallow Heap"};
    private static final String[] EMPTY_QUERY_HEADER = {"Enter query", "", ""};
    private final ClassesInfoPresenter presenter;
    private final JTable dataTable;
    private TableModelListener queryListener = new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent event) {
            if (event.getFirstRow() == 0 && event.getColumn() == 0) {
                String query = dataTable.getModel().getValueAt(0, 0).toString();
                presenter.onQueryByName(query);
            }
        }
    };

    public ClassesInfoPanel(@Nonnull MemoryDump data) {
        super(new BorderLayout());

        dataTable = new ClassesInfoTable();
        JScrollPane scroller = new JScrollPane(dataTable);
        add(scroller, BorderLayout.CENTER);

        ClassProvider clsProvider = new ClassProvider(data);
        InstanceProvider instanceProvider = new InstanceProvider(data);
        presenter = new ClassesInfoPresenterImpl(this, clsProvider, instanceProvider);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showReadyToQuery() {
        Object[][] cells = new Object[][]{EMPTY_QUERY_HEADER};
        dataTable.setModel(new DefaultTableModel(cells, HEADER));
        dataTable.getModel().addTableModelListener(queryListener);
    }

    @Override
    public void showQueryResult(List<ClassInfo> result, @Nonnull String query) {
        Object[][] cells = new Object[result.size() + 1][3];
        cells[0][0] = query;
        cells[0][1] = "";
        cells[0][2] = "";
        for (int i = 0; i < result.size(); i++) {
            ClassInfo cls = result.get(i);
            cells[i + 1][0] = cls.name;
            cells[i + 1][1] = cls.instanceCount;
            cells[i + 1][2] = cls.instanceSize * cls.instanceCount;
        }
        final InstanceTableModel model = new InstanceTableModel(cells);
        dataTable.setModel(model);
        model.addTableModelListener(queryListener);
        dataTable.setRowSorter(new InstanceTableRowSorter(model, query));
    }
}
