package com.badoo.hprof.viewer.viewfactory;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of an ArrayMap
 *
 * Created by Erik Andre on 05/12/15.
 */
public class ArrayMapClassDef extends BaseClassDef {

    final ClassDefinition cls;
    final InstanceField size;
    final InstanceField array;

    public ArrayMapClassDef(@Nonnull DumpData data) {
        cls = findClassByName("android.util.ArrayMap", data);
        size = findFieldByName("mSize", BasicType.INT, cls, data);
        array = findFieldByName("mArray", BasicType.OBJECT, cls, data);
    }
}
