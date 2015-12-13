package com.badoo.hprof.viewer.provider;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;

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
            ClassDefinition cls = data.classes.get(instance.getClassObjectId());
            List<Instance> list = instancesByClass.get(cls);
            if (list == null) {
                list = new ArrayList<Instance>();
                instancesByClass.put(cls, list);
            }
            list.add(instance);
        }
    }

    public ClassDefinition getClass(@Nonnull Instance instance) {
        return data.classes.get(instance.getClassObjectId());
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
}
