package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;




/**
 * Class definition for accessing data of an instance dump of a ColorDrawable
 * <p/>
 * Created by Erik Andre on 05/12/15.
 */
public abstract class ColorDrawableClassDef extends BaseClassDef {

    public static class StateClassDef extends BaseClassDef {

        public final ClassDefinition cls;
        public final InstanceField baseColor;

        StateClassDef(@Nonnull MemoryDump data, @Nonnull String colorFieldName) {
            cls = data.findClassByName("android.graphics.drawable.ColorDrawable$ColorState");
            baseColor = data.findFieldByName(colorFieldName, BasicType.INT, cls);
        }
    }
    public final ClassDefinition cls;
    public final InstanceField stateField;
    public final StateClassDef state;

    public ColorDrawableClassDef(@Nonnull MemoryDump data, @Nonnull String stateFieldName, @Nonnull String colorFieldName) {
        cls = data.findClassByName("android.graphics.drawable.ColorDrawable");
        stateField = data.findFieldByName(stateFieldName, BasicType.OBJECT, cls);
        state = new StateClassDef(data, colorFieldName);

    }
}
