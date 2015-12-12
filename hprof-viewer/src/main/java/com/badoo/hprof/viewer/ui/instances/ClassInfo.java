package com.badoo.hprof.viewer.ui.instances;

import com.badoo.hprof.library.model.ClassDefinition;

/**
 * Model used to populate the Instance info panel
 * Created by Erik Andre on 12/12/15.
 */
public class ClassInfo {

    public final ClassDefinition cls;
    public final String name;
    public final int instanceCount;

    public ClassInfo(ClassDefinition cls, String name, int instanceCount) {
        this.cls = cls;
        this.name = name;
        this.instanceCount = instanceCount;
    }
}
