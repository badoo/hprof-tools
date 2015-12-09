package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.factory.classdefs.BooleanClassDef;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Factory class for creating Booleans from Boolean instance dumps.
 * <p/>
 * Created by Erik Andre on 08/12/15.
 */
public class BooleanFactory extends BaseClassFactory<BooleanClassDef, Boolean> {

    private static BooleanFactory instance;

    private BooleanFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
    }

    @Nonnull
    public static BooleanFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        if (instance == null) {
            instance = new BooleanFactory(data, env);
        }
        return instance;
    }

    @Override
    protected Boolean create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull BooleanClassDef classDef) throws IOException {
        return instance.getBooleanField(classDef.value, data.classes);
    }

    @Nonnull
    @Override
    protected BooleanClassDef createClassDef(@Nonnull MemoryDump data) {
        return new BooleanClassDef(data);
    }
}
