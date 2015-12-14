package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;


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
        string = data.findClassByName("java.lang.String");
        integer = data.findClassByName("java.lang.Integer");
        bool = data.findClassByName("java.lang.Boolean");
        bundle = data.findClassByName("android.os.Bundle");
        enumCls = data.findClassByName("java.lang.Enum");
    }
}
