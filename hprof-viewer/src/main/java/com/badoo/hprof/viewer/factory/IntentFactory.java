package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.android.Bundle;
import com.badoo.hprof.viewer.android.Intent;
import com.badoo.hprof.viewer.factory.classdefs.IntentClassDef;

import java.io.IOException;
import java.util.Collections;

import javax.annotation.Nonnull;

/**
 * Factory class for creating Intent based on instance dumps
 * <p/>
 * Created by Erik Andre on 08/12/15.
 */
public class IntentFactory extends BaseClassFactory<IntentClassDef, Intent> {

    private static IntentFactory instance;

    public static IntentFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        if (instance == null) {
            instance = new IntentFactory(data, env);
        }
        return instance;
    }

    private IntentFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
    }

    @Override
    protected Intent create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull IntentClassDef classDef) throws IOException {
        Instance actionInstance = data.instances.get(instance.getObjectField(classDef.action, data.classes));
        String action = StringFactory.getInstance(data, env).create(actionInstance);
        Instance bundleInstance = data.instances.get(instance.getObjectField(classDef.extras, data.classes));
        Bundle extras = BundleFactory.getInstance(data, env).create(bundleInstance);
        if (extras == null) {
            extras = new Bundle(Collections.emptyMap());
        }
        return new Intent(action, extras);
    }

    @Nonnull
    @Override
    protected IntentClassDef createClassDef(@Nonnull MemoryDump data) {
        return new IntentClassDef(data);
    }
}
