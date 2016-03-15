package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.android.Bundle;
import com.badoo.hprof.viewer.factory.classdefs.BundleClassDef;
import com.badoo.hprof.viewer.factory.classdefs.LegacyBundleClassDef;
import com.badoo.hprof.viewer.factory.classdefs.LollipopBundleClassDef;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Factory for creating Bundles from Bundle instance dumps
 * <p/>
 * Created by Erik Andre on 08/12/15.
 */
public class BundleFactory extends BaseClassFactory<BundleClassDef, Bundle> {

    private static BundleFactory instance;

    public static BundleFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        if (instance == null) {
            instance = new BundleFactory(data, env);
        }
        return instance;
    }

    private final BundleClassDef classDef;

    private BundleFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
        // Pre-create class def so that we can return later
        if (env.sdkVersion >= 21) {
            classDef = new LollipopBundleClassDef(data);
        }
        else {
            classDef = new LegacyBundleClassDef(data);
        }
    }

    @Override
    protected Bundle create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull BundleClassDef classDef) throws IOException {
        Instance mapInstance = data.instances.get(instance.getObjectField(classDef.map, data.classes));
        Map<Object, Object> map = MapFactory.getInstance(data, env).create(mapInstance);
        if (map == null) {
            map = Collections.emptyMap();
        }
        return new Bundle(map);
    }

    @Nonnull
    @Override
    protected BundleClassDef createClassDef(@Nonnull MemoryDump data) {
        return classDef;
    }
}
