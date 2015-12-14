package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.viewer.MemoryDump;
import javax.annotation.Nonnull;

/**
 * Class definition for reading different types of maps
 *
 * Created by Erik Andre on 09/12/15.
 */
public class MapClassDef extends BaseClassDef {

    public final ClassDefinition arrayMap;
    public final ClassDefinition hashMap;

    public MapClassDef(@Nonnull MemoryDump data) {
        arrayMap = data.tryFindClassByName("android.util.ArrayMap");
        hashMap = data.tryFindClassByName("java.util.HashMap");
    }

}
