package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.factory.classdefs.CharSequenceClassDef;
import com.badoo.hprof.viewer.factory.classdefs.ClassUtils;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Factory for creating CharSequence (and it's subclasses) from instance dump data
 * <p/>
 * Created by Erik Andre on 09/12/15.
 */
public class CharSequenceFactory extends BaseClassFactory<CharSequenceClassDef, CharSequence> {

    private static CharSequenceFactory instance;

    public static CharSequenceFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        if (instance == null) {
            instance = new CharSequenceFactory(data, env);
        }
        return instance;
    }

    private CharSequenceFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
    }

    @Override
    protected CharSequence create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull CharSequenceClassDef classDef) throws IOException {
        if (ClassUtils.isInstanceOf(instance, classDef.string, data)) {
            return StringFactory.getInstance(data, env).create(instance);
        }
        return null;
    }

    @Nonnull
    @Override
    protected CharSequenceClassDef createClassDef(@Nonnull MemoryDump data) {
        return new CharSequenceClassDef(data);
    }
}
