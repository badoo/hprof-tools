package com.badoo.bmd.decruncher;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.annotation.Nonnull;

/**
 * Utility class for reading strings (class names) from an JAR file.
 * 
 * Created by Erik Andre on 17/12/14.
 */
public class JarStringReader {

    public static Set<String> readStrings(@Nonnull File in) throws IOException {
        Set<String> strings = new HashSet<String>();
        JarFile file = new JarFile(in);
        for(JarEntry entry : Collections.list(file.entries())) {
            String name = entry.getName();
            if (!name.endsWith(".class")) {
                continue;
            }
            name = name.substring(0, name.length() - 6);
            strings.add(name.replace(File.pathSeparator, "."));
        }
        return strings;
    }
}
