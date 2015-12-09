package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.factory.classdefs.IntegerClassDef;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Factory class for creating Integers from Integer instance dumps.
 * <p/>
 * Created by Erik Andre on 08/12/15.
 */
public class IntegerFactory extends BaseClassFactory<IntegerClassDef, Integer> {

    private static IntegerFactory instance;

    private IntegerFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
    }

    @Nonnull
    public static IntegerFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        if (instance == null) {
            instance = new IntegerFactory(data, env);
        }
        return instance;
    }

    @Override
    protected Integer create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull IntegerClassDef classDef) throws IOException {
        return instance.getIntField(classDef.value, data.classes);
    }

    @Nonnull
    @Override
    protected IntegerClassDef createClassDef(@Nonnull MemoryDump data) {
        return new IntegerClassDef(data);
    }
}
