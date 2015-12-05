package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of an Boolean
 *
 * Created by Erik Andre on 05/12/15.
 */
public class EnumClassDef extends BaseClassDef {

    final ClassDefinition cls;
    final InstanceField name;
    final InstanceField ordinal;

    public EnumClassDef(@Nonnull DumpData data) {
        cls = findClassByName("java.lang.Enum", data);
        name = findFieldByName("name", BasicType.OBJECT, cls, data);
        ordinal = findFieldByName("ordinal", BasicType.INT, cls, data);
    }
}
