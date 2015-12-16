package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;



/**
 * Class definition for accessing data of an instance dump of a ViewGroup
 *
 * Created by Erik Andre on 05/12/15.
 */
public class ViewGroupClassDef extends GenericViewClassDef {

    public final ClassDefinition viewGroupCls;
    public final InstanceField children;
    public final InstanceField childrenCount;

    public ViewGroupClassDef(@Nonnull MemoryDump data) {
        super(data);
        viewGroupCls = data.findClassByName("android.view.ViewGroup");
        children = data.findFieldByName("mChildren", BasicType.OBJECT, viewGroupCls);
        childrenCount = data.findFieldByName("mChildrenCount", BasicType.INT, viewGroupCls);
    }
}
