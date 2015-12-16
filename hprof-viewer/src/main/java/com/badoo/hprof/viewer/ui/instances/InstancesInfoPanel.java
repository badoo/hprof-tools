package com.badoo.hprof.viewer.ui.instances;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.provider.ClassProvider;
import com.badoo.hprof.viewer.provider.InstanceProvider;
import com.badoo.hprof.viewer.ui.classinfo.ClassInfo;

import java.awt.BorderLayout;
import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.DefaultRowSorter;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


/**
 * Created by Erik Andre on 13/12/15.
 */
public class InstancesInfoPanel extends JPanel implements InstancesInfoPresenter.View {

    private static final String[] HEADER = {"Name", "Shallow Heap"};

    private final JTable dataTable;
    private final InstancesInfoPresenterImpl presenter;

    public InstancesInfoPanel(@Nonnull MemoryDump data, @Nonnull List<Instance> instances) {
        super(new BorderLayout());
        dataTable = new JTable();
        dataTable.setAutoCreateRowSorter(true);
        JScrollPane scroller = new JScrollPane(dataTable);
        add(scroller, BorderLayout.CENTER);

        ClassProvider clsProvider = new ClassProvider(data);
        InstanceProvider instanceProvider = new InstanceProvider(data);
        presenter = new InstancesInfoPresenterImpl(this, instances, clsProvider, instanceProvider);
    }

    @Override
    public void showInstances(List<InstanceInfo> instances) {
        Object[][] cells = new Object[instances.size()][2];
        for (int i = 0; i < instances.size(); i++) {
            InstanceInfo instance = instances.get(i);
            cells[i][0] = instance.name;
            cells[i][1] = instance.size;
        }
        final DefaultTableModel model = new DefaultTableModel(cells, HEADER);
        dataTable.setModel(model);
    }
}
