package com.badoo.hprof.viewer.ui.classinfo;

import com.badoo.hprof.library.model.ClassDefinition;

/**
 * Model used to populate the Instance info panel
 * Created by Erik Andre on 12/12/15.
 */
public class ClassInfo {

    public final ClassDefinition cls;
    public final String name;
    public final int instanceCount;
    public final int instanceSize;

    public ClassInfo(ClassDefinition cls, String name, int instanceCount, int instanceSize) {
        this.cls = cls;
        this.name = name;
        this.instanceCount = instanceCount;
        this.instanceSize = instanceSize;
    }

    @Override
    public String toString() {
        return name;
    }
}
