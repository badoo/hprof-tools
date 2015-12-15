package com.badoo.hprof.viewer.ui.instances;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.provider.ClassProvider;
import com.badoo.hprof.viewer.provider.InstanceProvider;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


/**
 * Panel for showing information about one ore more instances
 * <p/>
 * Created by Erik Andre on 13/12/15.
 */
public class InstancesInfoPanel extends JPanel implements InstancesInfoPresenter.View {

    private static final String[] INSTANCES_HEADER = {"Name", "Shallow Heap"};
    private static final String[] DETAILS_HEADER = {"Name", "Value"};

    private final JTable instancesTable;
    private final JTable detailsTable;
    private final InstancesInfoPresenterImpl presenter;
    @SuppressWarnings("FieldCanBeLocal")
    private final MouseListener instanceSelectionListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            final int row = instancesTable.rowAtPoint(mouseEvent.getPoint());
            InstanceInfo instance = (InstanceInfo) instancesTable.getValueAt(row, 0);
            presenter.onInstanceSelected(instance);
        }
    };

    public InstancesInfoPanel(@Nonnull MemoryDump data, @Nonnull List<Instance> instances) {
        super(new BorderLayout());

        // Instances table
        instancesTable = new JTable();
        instancesTable.setAutoCreateRowSorter(true);
        instancesTable.setRowSelectionAllowed(true);
        instancesTable.addMouseListener(instanceSelectionListener);
        JScrollPane dataTableScrollPane = new JScrollPane(instancesTable);

        // Instance details table
        detailsTable = new JTable();
        detailsTable.setRowSelectionAllowed(true);
        JScrollPane detailsScrollPane = new JScrollPane(detailsTable);

        // Split pane dividing the instance details and the instances table
        JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, detailsScrollPane, dataTableScrollPane);
        splitter.setDividerLocation(300);
        add(splitter, BorderLayout.CENTER);

        ClassProvider clsProvider = new ClassProvider(data);
        InstanceProvider instanceProvider = new InstanceProvider(data);
        presenter = new InstancesInfoPresenterImpl(this, instances, clsProvider, instanceProvider);
        showInstanceDetails(Collections.emptyMap());
    }

    @Override
    public void showInstances(@Nonnull List<InstanceInfo> instances) {
        Object[][] cells = new Object[instances.size()][2];
        for (int i = 0; i < instances.size(); i++) {
            InstanceInfo instance = instances.get(i);
            cells[i][0] = instance;
            cells[i][1] = instance.size;
        }
        final DefaultTableModel model = new DefaultTableModel(cells, INSTANCES_HEADER);
        instancesTable.setModel(model);
    }

    @Override
    public void showInstanceDetails(@Nonnull Map<Object, Object> fields) {
        Object[][] cells = new Object[fields.size()][2];
        int count = 0;
        for (Object name : fields.keySet()) {
            cells[count][0] = name;
            cells[count][1] = fields.get(name);
            count++;
        }
        detailsTable.setModel(new DefaultTableModel(cells, DETAILS_HEADER));
    }
}
