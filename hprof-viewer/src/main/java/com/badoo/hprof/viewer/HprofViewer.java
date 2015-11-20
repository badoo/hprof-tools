package com.badoo.hprof.viewer;

import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.model.ViewGroup;
import com.badoo.hprof.viewer.rendering.ViewRenderer;
import com.badoo.hprof.viewer.ui.ImagePanel;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

/**
 * Entry point for the HPROF Viewer application.
 * <p/>
 * HPROF Viewer supports the following functionality:
 * <p/>
 * TODO
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
        DumpData data = new DumpData(processor.getClasses(), processor.getStrings(), processor.getInstances(),
            processor.getObjectArrays(), processor.getPrimitiveArrays());

        // Class data read, now we can figure out which classes are Views (or ViewGroups)
        Map<Integer, ClassDefinition> viewClasses = filterViewClasses(data);
        System.out.println("Found " + viewClasses.size() + " View classes");

        // Filter out the instances dumps of the View classes
        List<Instance> viewInstances = filterViewInstances(data, viewClasses);
        System.out.println("Found " + viewInstances.size() + " instances of View classes");

        // Find the View roots (Decor views), there should be at least one
        ClassDefinition decorClass = findDecorClass(viewClasses, data);
        List<Instance> viewRoots = findViewRoots(viewInstances, decorClass);
        System.out.println("Found " + viewRoots.size() + " roots instances of " + data.strings.get(decorClass.getNameStringId()).getValue());

        // Build the View hierarchy, starting with the roots
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<ViewGroup> roots = new ArrayList<ViewGroup>();
        for (Instance root : viewRoots) {
            ViewGroup viewRoot = ViewFactory.buildViewHierarchy(root, data);
            roots.add(viewRoot);
        }
        // Render the views
        BufferedImage image = ViewRenderer.renderViews(roots.get(1)); // Just the first one for now
        showImage(image);
    }

    private static void showImage(final BufferedImage image) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Create and set up the window.
                JFrame frame = new JFrame("Hprof Viewer");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                ImagePanel imagePanel = new ImagePanel(image);
                frame.getContentPane().add(imagePanel);

                //Display the window.
                frame.pack();
                frame.setVisible(true);
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

    private static ClassDefinition findDecorClass(Map<Integer, ClassDefinition> viewClasses, DumpData data) {
        for (ClassDefinition cls : viewClasses.values()) {
            HprofString clsName = data.strings.get(cls.getNameStringId());
            if (clsName.getValue().endsWith("$DecorView")) {
                return cls;
            }
        }
        throw new IllegalStateException("Dump contained no decor views!");
    }

    private static Map<Integer, ClassDefinition> filterViewClasses(DumpData data) {
        Map<Integer, ClassDefinition> viewClasses = new HashMap<Integer, ClassDefinition>();
        for (ClassDefinition cls : data.classes.values()) {
            if (isView(cls, data)) {
                viewClasses.put(cls.getObjectId(), cls);
            }
        }
        return viewClasses;
    }

    private static List<Instance> filterViewInstances(DumpData data, Map<Integer, ClassDefinition> viewClasses) {
        List<Instance> viewInstances = new ArrayList<Instance>();
        for (Instance instance : data.instances.values()) {
            if (viewClasses.containsKey(instance.getClassObjectId())) {
                viewInstances.add(instance);
            }
        }
        return viewInstances;
    }

    private static boolean isView(ClassDefinition cls, DumpData data) {
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
