package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;



/**
 * Base class definition for Bundles, extended by version specific implementations.
 *
 * Created by Erik Andre on 08/12/15.
 */
public abstract class BundleClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField map;

    protected BundleClassDef(@Nonnull ClassDefinition cls, @Nonnull MemoryDump data) {
        this.cls = cls;
        this.map = data.findFieldByName("mMap", BasicType.OBJECT, cls);
    }
}
