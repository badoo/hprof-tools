package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;
import static com.badoo.hprof.viewer.factory.classdefs.ClassUtils.findClassByName;
import static com.badoo.hprof.viewer.factory.classdefs.ClassUtils.findFieldByName;

/**
 * Class definition for accessing data of an instance dump of a Bitmap
 *
 * Created by Erik Andre on 05/12/15.
 */
public class BitmapClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField buffer;
    public final InstanceField width;
    public final InstanceField height;

    public BitmapClassDef(@Nonnull MemoryDump data) {
        cls = findClassByName("android.graphics.Bitmap", data);
        buffer = findFieldByName("mBuffer", BasicType.OBJECT, cls, data);
        width = findFieldByName("mWidth", BasicType.INT, cls, data);
        height = findFieldByName("mHeight", BasicType.INT, cls, data);
    }
}
