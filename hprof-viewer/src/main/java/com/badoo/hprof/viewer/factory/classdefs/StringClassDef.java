package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;


/**
 * Class definition for accessing data of an instance dump of a String
 *
 * Created by Erik Andre on 05/12/15.
 */
public class StringClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField value;
    public final InstanceField offset;
    public final InstanceField count;

    public StringClassDef(@Nonnull MemoryDump data) {
        cls = data.findClassByName("java.lang.String");
        value = data.findFieldByName("value", BasicType.OBJECT, cls);
        offset = data.tryFindFieldByName("offset", BasicType.INT, cls);
        count = data.findFieldByName("count", BasicType.INT, cls);
    }
}
