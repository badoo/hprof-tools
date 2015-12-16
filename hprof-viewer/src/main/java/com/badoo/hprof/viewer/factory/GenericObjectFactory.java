package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.factory.classdefs.GenericObjectClassDef;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Factory for creating generic objects of unknown type
 * <p/>
 * Created by Erik Andre on 08/12/15.
 */
public class GenericObjectFactory extends BaseClassFactory<GenericObjectClassDef, Object> {

    private static GenericObjectFactory instance;

    private GenericObjectFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
    }

    public static GenericObjectFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        if (instance == null) {
            instance = new GenericObjectFactory(data, env);
        }
        return instance;
    }

    @Override
    protected Object create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull GenericObjectClassDef classDef) throws IOException {
        if (data.isInstanceOf(instance, classDef.string)) {
            return StringFactory.getInstance(data, env).create(instance);
        }
        else if (data.isInstanceOf(instance, classDef.bool)) {
            return BooleanFactory.getInstance(data, env).create(instance);
        }
        else if (data.isInstanceOf(instance, classDef.integer)) {
            return IntegerFactory.getInstance(data, env).create(instance);
        }
        else if (data.isInstanceOf(instance, classDef.enumCls)) {
            return EnumFactory.getInstance(data, env).create(instance);
        }
        else if (data.isInstanceOf(instance, classDef.bundle)) {
            return BundleFactory.getInstance(data, env).create(instance);
        }
        return data.getClassName(instance);
    }

    @Nonnull
    @Override
    protected GenericObjectClassDef createClassDef(@Nonnull MemoryDump data) {
        return new GenericObjectClassDef(data);
    }
}
