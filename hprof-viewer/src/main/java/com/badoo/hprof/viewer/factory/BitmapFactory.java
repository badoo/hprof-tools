package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.model.PrimitiveArray;
import com.badoo.hprof.viewer.BitmapCache;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.factory.classdefs.BitmapClassDef;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Created by Erik Andre on 08/12/15.
 */
public class BitmapFactory extends BaseClassFactory<BitmapClassDef, BufferedImage> {

    private static BitmapFactory instance;

    public BitmapFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
    }

    public static BitmapFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        if (instance == null) {
            instance = new BitmapFactory(data, env);
        }
        return instance;
    }

    @Override
    protected BufferedImage create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull BitmapClassDef classDef) throws IOException {
        int width = instance.getIntField(classDef.width, data.classes);
        int height = instance.getIntField(classDef.height, data.classes);
        PrimitiveArray buffer = data.primitiveArrays.get(instance.getObjectField(classDef.buffer, data.classes));
        if (buffer != null) {
            return BitmapCache.createBitmap(instance.getObjectId(), buffer.getArrayData(), width, height);
        }
        return null;
    }

    @Nonnull
    @Override
    protected BitmapClassDef createClassDef(@Nonnull MemoryDump data) {
        return new BitmapClassDef(data);
    }
}
