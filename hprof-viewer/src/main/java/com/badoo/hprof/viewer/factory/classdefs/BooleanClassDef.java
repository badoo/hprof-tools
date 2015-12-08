package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;
import com.badoo.hprof.viewer.factory.classdefs.BaseClassDef;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of an Boolean
 *
 * Created by Erik Andre on 05/12/15.
 */
public class BooleanClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField value;

    public BooleanClassDef(@Nonnull DumpData data) {
        cls = findClassByName("java.lang.Boolean", data);
        value = findFieldByName("value", BasicType.BOOLEAN, cls, data);
    }
}
