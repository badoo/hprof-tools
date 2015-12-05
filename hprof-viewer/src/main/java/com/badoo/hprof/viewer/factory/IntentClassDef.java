package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of an Intent
 *
 * Created by Erik Andre on 05/12/15.
 */
class IntentClassDef extends BaseClassDef {

    final ClassDefinition cls;
    final InstanceField extras;
    final InstanceField action;

    public IntentClassDef(@Nonnull DumpData data) {
        cls = findClassByName("android.content.Intent", data);
        extras = findFieldByName("mExtras", BasicType.OBJECT, cls, data);
        action = findFieldByName("mAction", BasicType.OBJECT, cls, data);
    }

}
