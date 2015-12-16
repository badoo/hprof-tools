package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;



/**
 * Class definition for accessing data of an instance dump of an Boolean
 *
 * Created by Erik Andre on 05/12/15.
 */
public class EnumClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField name;
    public final InstanceField ordinal;

    public EnumClassDef(@Nonnull MemoryDump data) {
        cls = data.findClassByName("java.lang.Enum");
        name = data.findFieldByName("name", BasicType.OBJECT, cls);
        ordinal = data.findFieldByName("ordinal", BasicType.INT, cls);
    }
}
