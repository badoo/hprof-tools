package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;
import static com.badoo.hprof.viewer.factory.classdefs.ClassUtils.findClassByName;
import static com.badoo.hprof.viewer.factory.classdefs.ClassUtils.findFieldByName;

/**
 * Class definition used for accessing fields of unknown objects (e.g values in a map or list)
 *
 * Created by Erik Andre on 08/12/15.
 */
public class GenericObjectClassDef extends BaseClassDef {

    public final ClassDefinition string;
    public final ClassDefinition integer;
    public final ClassDefinition bool;
    public final ClassDefinition bundle;
    public final ClassDefinition enumCls;

    public GenericObjectClassDef(@Nonnull MemoryDump data) {
        string = findClassByName("java.lang.String", data);
        integer = findClassByName("java.lang.Integer", data);
        bool = findClassByName("java.lang.Boolean", data);
        bundle = findClassByName("android.os.Bundle", data);
        enumCls = findClassByName("java.lang.Enum", data);
    }
}
