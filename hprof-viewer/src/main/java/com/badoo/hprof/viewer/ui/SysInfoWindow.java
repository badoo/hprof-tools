package com.badoo.hprof.viewer.ui;

import com.badoo.hprof.viewer.android.Location;
import com.badoo.hprof.viewer.factory.SystemInfo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Window containing additional system information, e.g socket connections, location, etc.
 * <p/>
 * Created by Erik Andre on 05/12/15.
 */
public class SysInfoWindow extends JFrame implements ItemListener {

    private static final String LOCATION = "Location";
    private static final String SOCKET_CONNECTIONS = "Socket connections";
    private static final String[] HEADER = new String[]{"Name", "Value"};
    private static final String[] LOCATION_HEADER = new String[]{"Latitude", "Longitude", "Accuracy", "Time", "Provider"};

    private final JTable details;
    private final SystemInfo sysInfo;

    public SysInfoWindow(SystemInfo sysInfo) {
        super("System Information");

        this.sysInfo = sysInfo;
        // Picker
        Vector<String> items = new Vector<String>();
        items.add(LOCATION);
        items.add(SOCKET_CONNECTIONS);
        JComboBox picker = new JComboBox(items);
        picker.addItemListener(this);

        // Details view
        details = new JTable();
        JScrollPane detailsContainer = new JScrollPane(details);

        JPanel main = new JPanel(new BorderLayout());
        main.add(picker, BorderLayout.NORTH);
        main.add(detailsContainer, BorderLayout.CENTER);
        add(main);
        update(LOCATION);
        setMinimumSize(new Dimension(400, 300));
        pack();
        setVisible(true);
    }

    private void update(String selected) {
        if (LOCATION.equals(selected)) {
            showLocationInfo();
        }
    }

    private void showLocationInfo() {
        List<Location> locations = sysInfo.getLocations();
        Object[][] cells = new Object[locations.size()][];
        for (int i = 0; i < locations.size(); i++) {
            Location loc = locations.get(i);
            String timestamp = DateFormat.getDateTimeInstance().format(new Date(loc.getTime()));
            cells[i] = new Object[] {loc.getLatitude(), loc.getLongitude(), loc.getAccuracy(), timestamp, loc.getProvider()};
        }
        details.setModel(new DefaultTableModel(cells, LOCATION_HEADER));
    }


    @Override
    public void itemStateChanged(ItemEvent itemEvent) {

    }
}
