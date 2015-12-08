package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;
import com.badoo.hprof.viewer.factory.classdefs.BaseClassDef;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of a View
 * <p/>
 * Created by Erik Andre on 05/12/15.
 */
public class ViewClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField left;
    public final InstanceField right;
    public final InstanceField top;
    public final InstanceField bottom;
    public final InstanceField flags;
    public final InstanceField background;
    public final InstanceField context;


    public ViewClassDef(@Nonnull DumpData data) {
        cls = findClassByName("android.view.View", data);
        left = findFieldByName("mLeft", BasicType.INT, cls, data);
        right = findFieldByName("mRight", BasicType.INT, cls, data);
        top = findFieldByName("mTop", BasicType.INT, cls, data);
        bottom = findFieldByName("mBottom", BasicType.INT, cls, data);
        flags = findFieldByName("mViewFlags", BasicType.INT, cls, data);
        background = findFieldByName("mBackground", BasicType.OBJECT, cls, data);
        context = findFieldByName("mContext", BasicType.OBJECT, cls, data);
    }
}
