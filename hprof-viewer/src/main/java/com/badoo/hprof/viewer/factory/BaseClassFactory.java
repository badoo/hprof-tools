package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.factory.classdefs.BaseClassDef;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Erik Andre on 08/12/15.
 */
public abstract class BaseClassFactory<ClassDef extends BaseClassDef, OutputType> {

    private final MemoryDump data;
    private final Environment env;
    private ClassDef classDef;

    public BaseClassFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        this.data = data;
        this.env = env;
    }

    public final OutputType create(@Nullable Instance instance) {
        if (instance == null) {
            return null;
        }
        try {
            return create(instance, data, env, getClassDef());
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to create instance", e);
        }
    }

    protected abstract OutputType create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull ClassDef classDef) throws IOException;

    private ClassDef getClassDef() {
        if (classDef == null) {
            classDef = createClassDef(data);
        }
        return classDef;
    }

    @Nonnull
    protected abstract ClassDef createClassDef(@Nonnull MemoryDump data);

}
