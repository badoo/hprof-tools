package com.badoo.hprof.viewer.viewfactory;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of a Bitmap
 *
 * Created by Erik Andre on 05/12/15.
 */
class BitmapClassDef extends BaseClassDef {

    final ClassDefinition cls;
    final InstanceField buffer;
    final InstanceField width;
    final InstanceField height;

    public BitmapClassDef(@Nonnull DumpData data) {
        cls = findClassByName("android.graphics.Bitmap", data);
        buffer = findFieldByName("mBuffer", BasicType.OBJECT, cls, data);
        width = findFieldByName("mWidth", BasicType.INT, cls, data);
        height = findFieldByName("mHeight", BasicType.INT, cls, data);
    }
}
