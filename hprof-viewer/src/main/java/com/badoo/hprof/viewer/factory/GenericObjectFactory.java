package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.factory.classdefs.GenericObjectClassDef;

import java.io.IOException;

import javax.annotation.Nonnull;

import static com.badoo.hprof.viewer.factory.FactoryUtils.getClassName;
import static com.badoo.hprof.viewer.factory.FactoryUtils.isInstanceOf;

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
        if (isInstanceOf(instance, classDef.string, data)) {
            return StringFactory.getInstance(data, env).create(instance);
        }
        else if (isInstanceOf(instance, classDef.bool, data)) {
            return BooleanFactory.getInstance(data, env).create(instance);
        }
        else if (isInstanceOf(instance, classDef.integer, data)) {
            return IntegerFactory.getInstance(data, env).create(instance);
        }
        else if (isInstanceOf(instance, classDef.enumCls, data)) {
            return EnumFactory.getInstance(data, env).create(instance);
        }
        else if (isInstanceOf(instance, classDef.bundle, data)) {
            return BundleFactory.getInstance(data, env).create(instance);
        }
        return getClassName(instance, data);
    }

    @Nonnull
    @Override
    protected GenericObjectClassDef createClassDef(@Nonnull MemoryDump data) {
        return new GenericObjectClassDef(data);
    }
}
