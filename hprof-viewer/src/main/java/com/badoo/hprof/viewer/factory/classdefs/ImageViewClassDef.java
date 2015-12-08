package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;
import com.badoo.hprof.viewer.factory.classdefs.BaseClassDef;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of a ImageView
 *
 * Created by Erik Andre on 05/12/15.
 */
public class ImageViewClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField drawable;

    public ImageViewClassDef(@Nonnull DumpData data) {
        cls = findClassByName("android.widget.ImageView", data);
        drawable = findFieldByName("mDrawable", BasicType.OBJECT, cls, data);
    }
}
