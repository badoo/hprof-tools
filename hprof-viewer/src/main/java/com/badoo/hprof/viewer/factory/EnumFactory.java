package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.android.AndroidEnum;
import com.badoo.hprof.viewer.factory.classdefs.EnumClassDef;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Factory class for creating Booleans from Boolean instance dumps.
 * <p/>
 * Created by Erik Andre on 08/12/15.
 */
public class EnumFactory extends BaseClassFactory<EnumClassDef, AndroidEnum> {

    private static EnumFactory instance;

    private EnumFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
    }

    @Nonnull
    public static EnumFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        if (instance == null) {
            instance = new EnumFactory(data, env);
        }
        return instance;
    }

    @Override
    protected AndroidEnum create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull EnumClassDef classDef) throws IOException {
        String clsName = data.getClassName(instance);
        Instance nameInstance = data.instances.get(instance.getObjectField(classDef.name, data.classes));
        String value = StringFactory.getInstance(data, env).create(nameInstance);
        int ordinal = instance.getIntField(classDef.ordinal, data.classes);
        return new AndroidEnum(clsName, value, ordinal);
    }

    @Nonnull
    @Override
    protected EnumClassDef createClassDef(@Nonnull MemoryDump data) {
        return new EnumClassDef(data);
    }
}
