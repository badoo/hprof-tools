package com.badoo.hprof.viewer.provider;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.viewer.MemoryDump;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Provider of class information.
 *
 * Created by Erik Andre on 12/12/15.
 */
public class ClassProvider extends BaseProvider {

    private final MemoryDump data;
    private final Map<ClassDefinition, String> classNames = new HashMap<ClassDefinition, String>();

    public ClassProvider(@Nonnull MemoryDump data) {
        this.data = data;
        for (ClassDefinition cls : data.classes.values()) {
            classNames.put(cls, data.strings.get(cls.getNameStringId()).getValue());
        }
        setStatus(ProviderStatus.LOADED);
    }

    public String getClassName(@Nonnull ClassDefinition cls) {
        return classNames.get(cls);
    }

    public List<ClassDefinition> getClassesMatchingQuery(@Nonnull String query) {
        List<ClassDefinition> result = new ArrayList<ClassDefinition>();
        for (ClassDefinition cls : data.classes.values()) {
            String className = data.strings.get(cls.getNameStringId()).getValue();
            if (className.contains(query)) {
                result.add(cls);
            }
        }
        return result;
    }
}
