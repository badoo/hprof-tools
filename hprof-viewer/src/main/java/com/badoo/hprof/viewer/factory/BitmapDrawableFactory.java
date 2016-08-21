package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.ID;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.android.drawables.BitmapDrawable;
import com.badoo.hprof.viewer.factory.classdefs.BitmapDrawableClassDef;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Factory for creating BitmapDrawables from classinfo dumps
 * <p/>
 * Created by Erik Andre on 08/12/15.
 */
public class BitmapDrawableFactory extends BaseClassFactory<BitmapDrawableClassDef, BitmapDrawable> {

    private static BitmapDrawableFactory instance;

    public static BitmapDrawableFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        if (instance == null) {
            instance = new BitmapDrawableFactory(data, env);
        }
        return instance;
    }

    public BitmapDrawableFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
    }

    @Override
    protected BitmapDrawable create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull BitmapDrawableClassDef classDef) throws IOException {
        ID stateId = instance.getObjectField(classDef.stateField, data.classes);
        Instance state = data.instances.get(stateId);
        Instance bitmapInstance = data.instances.get(state.getObjectField(classDef.state.bitmap, data.classes));
        BufferedImage bitmap = BitmapFactory.getInstance(data, env).create(bitmapInstance);
        return bitmap != null? new BitmapDrawable(bitmap) : null;
    }

    @Nonnull
    @Override
    protected BitmapDrawableClassDef createClassDef(@Nonnull MemoryDump data) {
        return new BitmapDrawableClassDef(data);
    }
}
