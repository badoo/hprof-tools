package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.android.Version;
import com.badoo.hprof.viewer.android.drawables.ColorDrawable;
import com.badoo.hprof.viewer.factory.classdefs.ColorDrawableClassDef;
import com.badoo.hprof.viewer.factory.classdefs.LegacyColorDrawableClassDef;
import com.badoo.hprof.viewer.factory.classdefs.LollipopColorDrawableClassDef;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Factory for creating ColorDrawables from classinfo dumps
 * <p/>
 * Created by Erik Andre on 08/12/15.
 */
public class ColorDrawableFactory extends BaseClassFactory<ColorDrawableClassDef, ColorDrawable> {

    private static ColorDrawableFactory instance;

    public static ColorDrawableFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        if (instance == null) {
            instance = new ColorDrawableFactory(data, env);
        }
        return instance;
    }

    private final ColorDrawableClassDef classDef;

    private ColorDrawableFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
        if (env.sdkVersion >= Version.LOLLIPOP) {
            classDef = new LollipopColorDrawableClassDef(data);
        }
        else {
            classDef = new LegacyColorDrawableClassDef(data);
        }
    }

    @Override
    protected ColorDrawable create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull ColorDrawableClassDef classDef) throws IOException {
        int stateId = instance.getObjectField(classDef.stateField, data.classes);
        Instance state = data.instances.get(stateId);
        int baseColor = state.getIntField(classDef.state.baseColor, data.classes);
        return new ColorDrawable(baseColor);
    }

    @Nonnull
    @Override
    protected ColorDrawableClassDef createClassDef(@Nonnull MemoryDump data) {
        return classDef;
    }
}
