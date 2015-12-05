package com.badoo.hprof.viewer.viewfactory;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of a BitmapDrawable
 * <p/>
 * Created by Erik Andre on 05/12/15.
 */
class BitmapDrawableClassDef extends BaseClassDef {

    final ClassDefinition cls;
    final InstanceField stateField;
    final StateClassDef state;

    public BitmapDrawableClassDef(@Nonnull DumpData data) {
        cls = findClassByName("android.graphics.drawable.BitmapDrawable", data);
        stateField = findFieldByName("mBitmapState", BasicType.OBJECT, cls, data);
        state = new StateClassDef(data);
    }

    public static class StateClassDef extends BaseClassDef {

        final ClassDefinition cls;
        final InstanceField bitmap;

        StateClassDef(@Nonnull DumpData data) {
            cls = findClassByName("android.graphics.drawable.BitmapDrawable$BitmapState", data);
            bitmap = findFieldByName("mBitmap", BasicType.OBJECT, cls, data);
        }
    }
}
