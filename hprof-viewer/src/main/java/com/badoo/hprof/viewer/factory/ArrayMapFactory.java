package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.model.ObjectArray;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.factory.classdefs.ArrayMapClassDef;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Factory class for creating Maps based on ArrayMap instance dumps.
 *
 * Created by Erik Andre on 08/12/15.
 */
public class ArrayMapFactory extends BaseClassFactory<ArrayMapClassDef, Map<Object, Object>> {

    private static ArrayMapFactory instance;

    public static ArrayMapFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        if (instance == null) {
            instance = new ArrayMapFactory(data, env);
        }
        return instance;
    }

    private ArrayMapFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
    }

    @Override
    protected Map<Object, Object> create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull ArrayMapClassDef classDef) throws IOException {
        int size = instance.getIntField(classDef.size, data.classes);
        ObjectArray array = data.objArrays.get(instance.getObjectField(classDef.array, data.classes));
        Map<Object, Object> values = new HashMap<Object, Object>();
        for (int i = 0; i < size; i++) {
            int keyObjectId = array.getElements()[i * 2];
            int valueObjectId = array.getElements()[i * 2 + 1];
            Object key = GenericObjectFactory.getInstance(data, env).create(data.instances.get(keyObjectId));
            Object value = GenericObjectFactory.getInstance(data, env).create(data.instances.get(valueObjectId));
            values.put(key, value);
        }
        return values;
    }

    @Nonnull
    @Override
    protected ArrayMapClassDef createClassDef(@Nonnull MemoryDump data) {
        return new ArrayMapClassDef(data);
    }
}
