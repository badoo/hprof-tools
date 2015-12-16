package com.badoo.hprof.viewer;

import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.factory.Environment;
import com.badoo.hprof.viewer.factory.Screen;
import com.badoo.hprof.viewer.factory.ScreenFactory;
import com.badoo.hprof.viewer.factory.SystemInfo;
import com.badoo.hprof.viewer.factory.SystemInfoFactory;
import com.badoo.hprof.viewer.ui.ScreenInfoPanel;
import com.badoo.hprof.viewer.ui.TabbedInfoWindow;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entry point for the HPROF Viewer application.
 */
public class HprofViewer {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("No HPROF file specified");
            return;
        }
        File inFile = new File(args[0]);
        try {
            readHprofFile(inFile);
        }
        catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void readHprofFile(File inFile) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(inFile));
        ViewDataProcessor processor = new ViewDataProcessor();
        HprofReader reader = new HprofReader(in, processor);
        while (reader.hasNext()) {
            reader.next();
        }
        MemoryDump data = new MemoryDump(processor.getClasses(), processor.getStrings(), processor.getInstances(),
            processor.getObjectArrays(), processor.getPrimitiveArrays());

        // Class data read, now we can figure out which classes are Views (or ViewGroups)
        Map<Integer, ClassDefinition> viewClasses = filterViewClasses(data);
        System.out.println("Found " + viewClasses.size() + " View classes");

        // Filter out the classinfo dumps of the View classes
        List<Instance> viewInstances = filterViewInstances(data, viewClasses);
        System.out.println("Found " + viewInstances.size() + " classinfo of View classes");

        // Find the View roots (Decor views), there should be at least one
        ClassDefinition decorClass = findDecorClass(viewClasses, data);
        List<Instance> viewRoots = findViewRoots(viewInstances, decorClass);
        System.out.println("Found " + viewRoots.size() + " roots classinfo of " + data.strings.get(decorClass.getNameStringId()).getValue());

        // Build the View hierarchy, starting with the roots
        List<Screen> screens = new ArrayList<Screen>();
        Environment env = new Environment(data);
        for (Instance root : viewRoots) {
            Screen screen = ScreenFactory.buildViewHierarchy(root, data, env);
            screens.add(screen);
        }
        // Collect system information
        SystemInfo sysInfo = SystemInfoFactory.createSystemInfo(data, env);
        // Render the views
        updateUi(screens, sysInfo, data);
    }

    private static void updateUi(final List<Screen> roots, final SystemInfo sysInfo, final MemoryDump data) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Create and set up the window.
                new TabbedInfoWindow(data, roots, sysInfo);
            }
        });
    }

    private static List<Instance> findViewRoots(List<Instance> viewInstances, ClassDefinition decorClass) {
        List<Instance> roots = new ArrayList<Instance>();
        for (Instance instance : viewInstances) {
            if (instance.getClassObjectId() == decorClass.getObjectId()) {
                roots.add(instance);
            }
        }
        return roots;
    }

    private static ClassDefinition findDecorClass(Map<Integer, ClassDefinition> viewClasses, MemoryDump data) {
        for (ClassDefinition cls : viewClasses.values()) {
            HprofString clsName = data.strings.get(cls.getNameStringId());
            if (clsName.getValue().endsWith("$DecorView")) {
                return cls;
            }
        }
        throw new IllegalStateException("Dump contained no decor views!");
    }

    private static Map<Integer, ClassDefinition> filterViewClasses(MemoryDump data) {
        Map<Integer, ClassDefinition> viewClasses = new HashMap<Integer, ClassDefinition>();
        for (ClassDefinition cls : data.classes.values()) {
            if (isView(cls, data)) {
                viewClasses.put(cls.getObjectId(), cls);
            }
        }
        return viewClasses;
    }

    private static List<Instance> filterViewInstances(MemoryDump data, Map<Integer, ClassDefinition> viewClasses) {
        List<Instance> viewInstances = new ArrayList<Instance>();
        for (Instance instance : data.instances.values()) {
            if (viewClasses.containsKey(instance.getClassObjectId())) {
                viewInstances.add(instance);
            }
        }
        return viewInstances;
    }

    private static boolean isView(ClassDefinition cls, MemoryDump data) {
        while (cls != null) {
            HprofString clsName = data.strings.get(cls.getNameStringId());
            if (clsName == null) {
                System.err.println("Missing string id: " + cls.getNameStringId());
                return false;
            }
            if ("android.view.View".equals(clsName.getValue())) {
                return true;
            }
            cls = data.classes.get(cls.getSuperClassObjectId());
        }
        return false;
    }

}
