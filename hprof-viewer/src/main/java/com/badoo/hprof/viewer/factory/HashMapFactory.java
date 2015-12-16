package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.model.ObjectArray;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.factory.classdefs.HashMapClassDef;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Factory class for creating HashMaps from instance dump data
 * <p/>
 * Created by Erik Andre on 09/12/15.
 */
public class HashMapFactory extends BaseClassFactory<HashMapClassDef, HashMap<Object, Object>> {

    private static HashMapFactory instance;

    public static HashMapFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        if (instance == null) {
            instance = new HashMapFactory(data, env);
        }
        return instance;
    }

    private HashMapFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
    }

    @Override
    protected HashMap<Object, Object> create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull HashMapClassDef classDef) throws IOException {
        ObjectArray table = data.objArrays.get(instance.getObjectField(classDef.table, data.classes));
        HashMap<Object, Object> result = new HashMap<Object, Object>();
        if (table != null) {
            for (int i = 0; i < table.getCount(); i++) {
                addEntry(data.instances.get(table.getElements()[i]), data, env, classDef.hashEntryClassDef, result);
            }
        }
        return result;
    }

    private void addEntry(@Nullable Instance entry, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull HashMapClassDef.HashEntryClassDef classDef, @Nonnull Map<Object, Object> map) throws IOException {
        if (entry == null) {
            return;
        }
        do {
            Instance keyInstance = data.instances.get(entry.getObjectField(classDef.key, data.classes));
            Object key = GenericObjectFactory.getInstance(data, env).create(keyInstance);
            Instance valueInstance = data.instances.get(entry.getObjectField(classDef.value, data.classes));
            Object value = GenericObjectFactory.getInstance(data, env).create(valueInstance);
            map.put(key, value);
            entry = data.instances.get(entry.getObjectField(classDef.next, data.classes));
        } while (entry != null);
    }

    @Nonnull
    @Override
    protected HashMapClassDef createClassDef(@Nonnull MemoryDump data) {
        return new HashMapClassDef(data);
    }
}
