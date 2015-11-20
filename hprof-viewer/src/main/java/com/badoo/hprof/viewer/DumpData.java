package com.badoo.hprof.viewer;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.model.ObjectArray;
import com.badoo.hprof.library.model.PrimitiveArray;

import java.util.Map;

/**
 * Data class for the information read from the HPROF file
 */
public class DumpData {

    final Map<Integer, HprofString> strings;
    final Map<Integer, ClassDefinition> classes;
    final Map<Integer, Instance> instances;
    final Map<Integer, ObjectArray> objArrays;
    final Map<Integer, PrimitiveArray> primitiveArrays;

    public DumpData(Map<Integer, ClassDefinition> classes, Map<Integer, HprofString> strings, Map<Integer, Instance> instances,
                    Map<Integer, ObjectArray> objArrays, Map<Integer, PrimitiveArray> primitiveArrays) {
        this.strings = strings;
        this.classes = classes;
        this.instances = instances;
        this.objArrays = objArrays;
        this.primitiveArrays = primitiveArrays;
    }
}
