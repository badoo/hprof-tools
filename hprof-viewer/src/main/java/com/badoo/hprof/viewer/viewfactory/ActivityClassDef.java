package com.badoo.hprof.viewer.viewfactory;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of an Activity
 *
 * Created by Erik Andre on 05/12/15.
 */
class ActivityClassDef extends BaseClassDef {

    final ClassDefinition cls;
    final InstanceField title;
    final InstanceField intent;


    public ActivityClassDef(@Nonnull DumpData data) {
        cls = findClassByName("android.app.Activity", data);
        title = findFieldByName("mTitle", BasicType.OBJECT, cls, data);
        intent = findFieldByName("mIntent", BasicType.OBJECT, cls, data);
    }
}
