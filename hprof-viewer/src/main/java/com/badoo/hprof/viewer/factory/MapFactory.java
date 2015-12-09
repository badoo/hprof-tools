package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.factory.classdefs.MapClassDef;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Nonnull;

import static com.badoo.hprof.viewer.factory.FactoryUtils.isInstanceOf;

/**
 * Factory for creating different types of maps.
 * <p/>
 * Created by Erik Andre on 09/12/15.
 */
public class MapFactory extends BaseClassFactory<MapClassDef, Map<Object, Object>> {

    private static MapFactory instance;

    public static MapFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        if (instance == null) {
            instance = new MapFactory(data, env);
        }
        return instance;
    }

    private MapFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
    }

    @Override
    protected Map<Object, Object> create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull MapClassDef classDef) throws IOException {
        if (isInstanceOf(instance, classDef.arrayMap, data)) {
            return ArrayMapFactory.getInstance(data, env).create(instance);
        }
        else if (isInstanceOf(instance, classDef.hashMap, data)) {
            return HashMapFactory.getInstance(data, env).create(instance);
        }
        return null;
    }

    @Nonnull
    @Override
    protected MapClassDef createClassDef(@Nonnull MemoryDump data) {
        return new MapClassDef(data);
    }
}
