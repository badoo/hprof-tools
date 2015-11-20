package com.badoo.hprof.viewer;

import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.model.Instance;

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
 *
 * HPROF Viewer supports the following functionality:
 *
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
        // Class data read, now we can figure out which classes are Views (or ViewGroups)
        Map<Integer, ClassDefinition> classes = processor.getClasses();
        Map<Integer, HprofString> strings = processor.getStrings();
        Map<Integer, ClassDefinition> viewClasses = new HashMap<Integer, ClassDefinition>();
        for (ClassDefinition cls : classes.values()) {
            if (isView(cls, classes, strings)) {
                viewClasses.put(cls.getObjectId(), cls);
            }
        }
        System.out.println("Found " + viewClasses.size() + " View classes");
        // Filter out the instances dumps of the View classes
        List<Instance> viewInstances = filterViewInstances(processor.getInstances(), viewClasses);
        System.out.println("Found " + viewInstances.size() + " instances of View classes");
        // Find the View roots (Decor views), there should be at least one
        ClassDefinition decorClass = findDecorClass(viewClasses, strings);
        List<Instance> viewRoots = findViewRoots(viewInstances, decorClass);
        System.out.println("Found " + viewRoots.size() + " roots instances of " + strings.get(decorClass.getNameStringId()).getValue());

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

    private static ClassDefinition findDecorClass(Map<Integer, ClassDefinition> viewClasses, Map<Integer, HprofString> strings) {
        for (ClassDefinition cls : viewClasses.values()) {
            HprofString clsName = strings.get(cls.getNameStringId());
            if (clsName.getValue().endsWith("$DecorView")) {
                return cls;
            }
        }
        throw new IllegalStateException("Dump contained no decor views!");
    }

    private static List<Instance> filterViewInstances(List<Instance> allInstances, Map<Integer, ClassDefinition> viewClasses) {
        List<Instance> viewInstances = new ArrayList<Instance>();
        for (Instance instance : allInstances) {
            if (viewClasses.containsKey(instance.getClassObjectId())) {
                viewInstances.add(instance);
            }
        }
        return viewInstances;
    }

    private static boolean isView(ClassDefinition cls, Map<Integer, ClassDefinition> allClasses, Map<Integer, HprofString> strings) {
        while (cls != null) {
            HprofString clsName = strings.get(cls.getNameStringId());
            if (clsName == null) {
                System.err.println("Missing string id: " + cls.getNameStringId());
                return false;
            }
            if ("android.view.View".equals(clsName.getValue())) {
                return true;
            }
            cls = allClasses.get(cls.getSuperClassObjectId());
        }
        return false;
    }

}
