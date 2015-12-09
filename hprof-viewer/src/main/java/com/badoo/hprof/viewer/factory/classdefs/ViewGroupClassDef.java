package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;
import static com.badoo.hprof.viewer.factory.classdefs.ClassUtils.findClassByName;
import static com.badoo.hprof.viewer.factory.classdefs.ClassUtils.findFieldByName;

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
        viewGroupCls = findClassByName("android.view.ViewGroup", data);
        children = findFieldByName("mChildren", BasicType.OBJECT, viewGroupCls, data);
        childrenCount = findFieldByName("mChildrenCount", BasicType.INT, viewGroupCls, data);
    }
}
