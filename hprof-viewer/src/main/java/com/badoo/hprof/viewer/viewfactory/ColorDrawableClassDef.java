package com.badoo.hprof.viewer.viewfactory;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of a ColorDrawable
 * <p/>
 * Created by Erik Andre on 05/12/15.
 */
class ColorDrawableClassDef extends BaseClassDef {

    final ClassDefinition cls;
    final InstanceField stateField;
    final StateClassDef state;

    public ColorDrawableClassDef(@Nonnull DumpData data) {
        cls = findClassByName("android.graphics.drawable.ColorDrawable", data);
        stateField = findFieldByName("mColorState", BasicType.OBJECT, cls, data);
        state = new StateClassDef(data);

    }

    public static class StateClassDef extends BaseClassDef {

        final ClassDefinition cls;
        final InstanceField baseColor;

        StateClassDef(@Nonnull DumpData data) {
            cls = findClassByName("android.graphics.drawable.ColorDrawable$ColorState", data);
            baseColor = findFieldByName("mBaseColor", BasicType.INT, cls, data);
        }
    }
}
