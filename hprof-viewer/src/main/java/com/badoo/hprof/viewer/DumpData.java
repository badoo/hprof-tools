package com.badoo.hprof.viewer;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;

import java.util.Map;

/**
 * Data class for the information read from the HPROF file
 */
public class DumpData {

    final Map<Integer, HprofString> strings;
    final Map<Integer, ClassDefinition> classes;

    public DumpData(Map<Integer, ClassDefinition> classes, Map<Integer, HprofString> strings) {
        this.strings = strings;
        this.classes = classes;
    }
}
