package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of a View
 * <p/>
 * Created by Erik Andre on 05/12/15.
 */
public class GenericViewClassDef extends BaseClassDef {

    public final ClassDefinition viewCls;
    public final InstanceField left;
    public final InstanceField right;
    public final InstanceField top;
    public final InstanceField bottom;
    public final InstanceField flags;
    public final InstanceField background;
    public final InstanceField context;


    public GenericViewClassDef(@Nonnull MemoryDump data) {
        viewCls = findClassByName("android.view.View", data);
        left = findFieldByName("mLeft", BasicType.INT, viewCls, data);
        right = findFieldByName("mRight", BasicType.INT, viewCls, data);
        top = findFieldByName("mTop", BasicType.INT, viewCls, data);
        bottom = findFieldByName("mBottom", BasicType.INT, viewCls, data);
        flags = findFieldByName("mViewFlags", BasicType.INT, viewCls, data);
        background = findFieldByName("mBackground", BasicType.OBJECT, viewCls, data);
        context = findFieldByName("mContext", BasicType.OBJECT, viewCls, data);
    }
}
