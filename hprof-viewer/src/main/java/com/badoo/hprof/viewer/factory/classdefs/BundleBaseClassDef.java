package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;
import com.badoo.hprof.viewer.factory.classdefs.BaseClassDef;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of an Intent
 *
 * Created by Erik Andre on 05/12/15.
 */
public class BundleBaseClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField map;

    public BundleBaseClassDef(@Nonnull DumpData data) {
        cls = findClassByName("android.os.BaseBundle", data);
        map = findFieldByName("mMap", BasicType.OBJECT, cls, data);
    }

}
