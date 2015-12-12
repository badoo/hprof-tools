package com.badoo.hprof.viewer.ui;

import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.factory.Screen;
import com.badoo.hprof.viewer.factory.SystemInfo;

import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Created by Erik Andre on 10/12/15.
 */
public class TabbedInfoWindow extends JFrame {

    private final JTabbedPane tabHost;

    public TabbedInfoWindow(@Nonnull MemoryDump data, @Nonnull List<Screen> screenList, SystemInfo sysInfo) {
        super("HprofViewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tabHost = new JTabbedPane();
        add(tabHost);

        tabHost.addTab("Activities", new ScreenInfoPanel(screenList));
        tabHost.addTab("System info", new SystemInfoPanel(sysInfo));
        tabHost.addTab("Instances", new JPanel());
        pack();
        setVisible(true);
    }



}
