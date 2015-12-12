package com.badoo.hprof.viewer.provider;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Provider for Instance data
 *
 * Created by Erik Andre on 12/12/15.
 */
public class InstanceProvider extends BaseProvider {

    private Map<ClassDefinition, List<Instance>> instancesByClass = new HashMap<ClassDefinition, List<Instance>>();

    public InstanceProvider(@Nonnull MemoryDump data) {
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

    public int getInstancesOfClass(@Nonnull ClassDefinition cls) {
        List<Instance> list = instancesByClass.get(cls);
        if (list == null) {
            return 0;
        }
        else {
            return list.size();
        }
    }
}
