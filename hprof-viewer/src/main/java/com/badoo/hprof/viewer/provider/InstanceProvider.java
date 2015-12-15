package com.badoo.hprof.viewer.provider;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.factory.Environment;
import com.badoo.hprof.viewer.factory.GenericObjectFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Provider for Instance data
 * <p/>
 * Created by Erik Andre on 12/12/15.
 */
public class InstanceProvider extends BaseProvider {

    private final MemoryDump data;
    private Map<ClassDefinition, List<Instance>> instancesByClass = new HashMap<ClassDefinition, List<Instance>>();

    public InstanceProvider(@Nonnull MemoryDump data) {
        this.data = data;
        for (Instance instance : data.instances.values()) {
            ClassDefinition cls = data.classes.get(instance.getClassId());
            List<Instance> list = instancesByClass.get(cls);
            if (list == null) {
                list = new ArrayList<Instance>();
                instancesByClass.put(cls, list);
            }
            list.add(instance);
        }
    }

    public ClassDefinition getClass(@Nonnull Instance instance) {
        return data.classes.get(instance.getClassId());
    }

    @Nonnull
    public List<Instance> getAllInstancesOf(@Nonnull ClassDefinition cls) {
        List<Instance> list = instancesByClass.get(cls);
        if (list == null) {
            return Collections.emptyList();
        }
        else {
            return list;
        }
    }

    public int getInstanceCountOfClass(@Nonnull ClassDefinition cls) {
        List<Instance> list = instancesByClass.get(cls);
        if (list == null) {
            return 0;
        }
        else {
            return list.size();
        }
    }

    @Nonnull
    public Map<Object, Object> getInstanceFields(@Nonnull Instance instance) throws IOException {
        HashMap<Object, Object> result = new HashMap<Object, Object>();
        ClassDefinition cls = getClass(instance);
        while (cls != null) {
            for (InstanceField field : cls.getInstanceFields()) {
                final String name = data.strings.get(field.getFieldNameId()).getValue();
                final Object value;
                switch (field.getType()) {
                    case OBJECT:
                        value = readObjectField(instance, field);
                        break;
                    default:
                        value = readPrimitiveField(instance, field);

                }
                result.put(name, value);
            }
            cls = data.classes.get(cls.getSuperClassObjectId());
        }
        return result;
    }

    private Object readPrimitiveField(Instance instance, InstanceField field) throws IOException {
        switch (field.getType()) {
            case INT:
                return instance.getIntField(field, data.classes);
            case BOOLEAN:
                return instance.getBooleanField(field, data.classes);
            case FLOAT:
                return instance.getFloatField(field, data.classes);
            case LONG:
                return instance.getLongField(field, data.classes);
            case BYTE:
                return instance.getByteField(field, data.classes);
            case DOUBLE:
                return instance.getDoubleField(field, data.classes);
            case CHAR:
                return instance.getCharField(field, data.classes);
            case SHORT:
                return instance.getShortField(field, data.classes);
        }
        return null;
    }

    private Object readObjectField(Instance instance, InstanceField field) throws IOException {
        Instance fieldInstance = data.instances.get(instance.getObjectField(field, data.classes));
        return GenericObjectFactory.getInstance(data, Environment.getEnvironment(data)).create(fieldInstance);
    }
}
