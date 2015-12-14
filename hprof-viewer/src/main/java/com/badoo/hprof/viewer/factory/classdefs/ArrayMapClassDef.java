package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of an ArrayMap
 * <p/>
 * Created by Erik Andre on 05/12/15.
 */
public class ArrayMapClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField size;
    public final InstanceField array;

    public ArrayMapClassDef(@Nonnull MemoryDump data) {
        cls = data.findClassByName("android.util.ArrayMap");
        size = data.findFieldByName("mSize", BasicType.INT, cls);
        array = data.findFieldByName("mArray", BasicType.OBJECT, cls);
    }
}
