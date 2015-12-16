package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.android.Drawable;
import com.badoo.hprof.viewer.factory.classdefs.DrawableClassDef;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Factory for creating Drawable classinfo of different types
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
        if (data.isInstanceOf(instance, classDef.colorDrawableCls)) {
            return ColorDrawableFactory.getInstance(data, env).create(instance);
        }
        else if (data.isInstanceOf(instance, classDef.bitmapDrawableCls)) {
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
