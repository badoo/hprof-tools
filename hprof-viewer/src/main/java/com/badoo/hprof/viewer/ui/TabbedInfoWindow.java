package com.badoo.hprof.viewer.ui;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.factory.Screen;
import com.badoo.hprof.viewer.factory.SystemInfo;
import com.badoo.hprof.viewer.ui.classinfo.ClassesInfoPanel;
import com.badoo.hprof.viewer.ui.instances.InstancesInfoPanel;

import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * Created by Erik Andre on 10/12/15.
 */
public class TabbedInfoWindow extends JFrame {

    private final JTabbedPane tabHost;
    private final MemoryDump data;

    public TabbedInfoWindow(@Nonnull MemoryDump data, @Nonnull List<Screen> screenList, SystemInfo sysInfo) {
        super("HprofViewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tabHost = new JTabbedPane();
        this.data = data;
        add(tabHost);

        tabHost.addTab("Activities", new ScreenInfoPanel(this, screenList));
        tabHost.addTab("System info", new SystemInfoPanel(sysInfo));
        tabHost.addTab("Classes", new ClassesInfoPanel(data, this));
        pack();
        setVisible(true);
    }

    public void showInstancesListTab(@Nonnull String name, List<Instance> instances) {
        tabHost.add(name, new InstancesInfoPanel(data, instances));
    }
}
