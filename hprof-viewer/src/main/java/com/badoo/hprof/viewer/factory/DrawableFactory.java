package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.android.Drawable;
import com.badoo.hprof.viewer.factory.classdefs.ClassUtils;
import com.badoo.hprof.viewer.factory.classdefs.DrawableClassDef;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Factory for creating Drawable instances of different types
 * <p/>
 * Created by Erik Andre on 08/12/15.
 */
public class DrawableFactory extends BaseClassFactory<DrawableClassDef, Drawable> {

    private static DrawableFactory instance;

    public static DrawableFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        if (instance == null) {
            instance = new DrawableFactory(data, env);
        }
        return instance;
    }

    private DrawableFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
    }

    @Override
    protected Drawable create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull DrawableClassDef classDef) throws IOException {
        if (ClassUtils.isInstanceOf(instance, classDef.colorDrawableCls, data)) {
            return ColorDrawableFactory.getInstance(data, env).create(instance);
        }
        else if (ClassUtils.isInstanceOf(instance, classDef.bitmapDrawableCls, data)) {
            return BitmapDrawableFactory.getInstance(data, env).create(instance);
        }
        return null; // Unsupported drawable type
    }

    @Nonnull
    @Override
    protected DrawableClassDef createClassDef(@Nonnull MemoryDump data) {
        return new DrawableClassDef(data);
    }
}
