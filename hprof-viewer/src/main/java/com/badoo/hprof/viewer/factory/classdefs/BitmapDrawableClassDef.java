package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of a BitmapDrawable
 * <p/>
 * Created by Erik Andre on 05/12/15.
 */
public class BitmapDrawableClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField stateField;
    public final StateClassDef state;

    public BitmapDrawableClassDef(@Nonnull MemoryDump data) {
        cls = data.findClassByName("android.graphics.drawable.BitmapDrawable");
        stateField = data.findFieldByName("mBitmapState", BasicType.OBJECT, cls);
        state = new StateClassDef(data);
    }

    public static class StateClassDef extends BaseClassDef {

        public final ClassDefinition cls;
        public final InstanceField bitmap;

        StateClassDef(@Nonnull MemoryDump data) {
            cls = data.findClassByName("android.graphics.drawable.BitmapDrawable$BitmapState");
            bitmap = data.findFieldByName("mBitmap", BasicType.OBJECT, cls);
        }
    }
}
