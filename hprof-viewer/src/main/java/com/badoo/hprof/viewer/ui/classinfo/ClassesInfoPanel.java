package com.badoo.hprof.viewer.ui.classinfo;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.provider.ClassProvider;
import com.badoo.hprof.viewer.provider.InstanceProvider;
import com.badoo.hprof.viewer.ui.TabbedInfoWindow;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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

    static class ClassesTableModel extends DefaultTableModel {

        public ClassesTableModel(Object[][] cells) {
            super(cells, HEADER);
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return row == 0;
        }
    }

    static class ClassesTableRowSorter extends TableRowSorter<ClassesTableModel> {

        public ClassesTableRowSorter(@Nonnull final ClassesTableModel model, @Nonnull String query) {
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

    class PopupListener extends MouseAdapter {

        private final JPopupMenu popup;

        public PopupListener(@Nonnull JPopupMenu popup) {
            this.popup = popup;
        }

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                final int row = dataTable.rowAtPoint(e.getPoint());
                if (row != 0) {
                    selectedItem = (ClassInfo) dataTable.getValueAt(row, 0);
                    popup.show(e.getComponent(),
                        e.getX(), e.getY());
                }
            }
        }
    }

    private static final String[] HEADER = {"Name", "Instances", "Shallow Heap"};
    private static final String[] EMPTY_QUERY_HEADER = {"Enter query", "", ""};
    private final ClassesInfoPresenter presenter;
    private final JTable dataTable;
    private final TabbedInfoWindow mainWindow;
    private TableModelListener queryListener = new TableModelListener() {
        @Override
        public void tableChanged(TableModelEvent event) {
            if (event.getFirstRow() == 0 && event.getColumn() == 0) {
                String query = dataTable.getModel().getValueAt(0, 0).toString();
                presenter.onQueryByName(query);
            }
        }
    };
    private ClassInfo selectedItem;

    public ClassesInfoPanel(@Nonnull MemoryDump data, @Nonnull TabbedInfoWindow mainWindow) {
        super(new BorderLayout());

        this.mainWindow = mainWindow;
        dataTable = new ClassesInfoTable();
        JScrollPane scroller = new JScrollPane(dataTable);
        dataTable.setRowSelectionAllowed(true);
        add(scroller, BorderLayout.CENTER);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem listWithOutgoingRefs = new JMenuItem("List with outgoing references");
        listWithOutgoingRefs.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                presenter.onListInstances(selectedItem);
            }
        });
        popupMenu.add(listWithOutgoingRefs);
        JMenuItem calculateRetainedHeap = new JMenuItem("Calculate retained heap size");
        popupMenu.add(calculateRetainedHeap);
        MouseListener popupListener = new PopupListener(popupMenu);
        dataTable.addMouseListener(popupListener);

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
            cells[i + 1][0] = cls;
            cells[i + 1][1] = cls.instanceCount;
            cells[i + 1][2] = cls.instanceSize * cls.instanceCount;
        }
        final ClassesTableModel model = new ClassesTableModel(cells);
        dataTable.setModel(model);
        model.addTableModelListener(queryListener);
        dataTable.setRowSorter(new ClassesTableRowSorter(model, query));
    }

    @Override
    public void showInstancesListTab(@Nonnull String name, @Nonnull List<Instance> instances) {
        mainWindow.showInstancesListTab(name, instances);
    }
}
