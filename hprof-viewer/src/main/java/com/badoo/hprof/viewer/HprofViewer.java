package com.badoo.hprof.viewer;

import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
        ViewDataProcessor classProcessor = new ViewDataProcessor();
        HprofReader reader = new HprofReader(in, classProcessor);
        while (reader.hasNext()) {
            reader.next();
        }
        // Class data read, now we can figure out which classes are Views (or ViewGroups)
        Map<Integer, ClassDefinition> classes = classProcessor.getClasses();
        Map<Integer, HprofString> strings = classProcessor.getStrings();
        List<ClassDefinition> viewClasses = new ArrayList<ClassDefinition>();
        for (ClassDefinition cls : classes.values()) {
            if (isView(cls, classes, strings)) {
                viewClasses.add(cls);
            }
        }
        System.out.println("Found " + viewClasses.size() + " View classes");
        //
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
