package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;


/**
 * Class definition for accessing data of an instance dump of an Activity
 * <p/>
 * Created by Erik Andre on 05/12/15.
 */
public class ActivityClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField title;
    public final InstanceField intent;


    public ActivityClassDef(@Nonnull MemoryDump data) {
        cls = data.findClassByName("android.app.Activity");
        title = data.findFieldByName("mTitle", BasicType.OBJECT, cls);
        intent = data.findFieldByName("mIntent", BasicType.OBJECT, cls);
    }
}
