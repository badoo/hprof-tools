package com.badoo.hprof.viewer;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.model.ViewGroup;

import java.io.IOException;
import java.util.List;

/**
 */
public class ViewFactory {

    private static class RefHolder {

        final ClassDefinition viewClass;
        final ClassDefinition viewGroupClass;
        final InstanceField childrenField;

        private RefHolder(DumpData data) {
            viewClass = findClassByName("android.view.View", data);
            viewGroupClass = findClassByName("android.view.ViewGroup", data);
            childrenField = findFieldByName("mChildren", BasicType.OBJECT, viewGroupClass, data);
        }
    }

    static ViewGroup buildViewHierarchy(Instance root, DumpData data) {
        RefHolder refs = new RefHolder(data);
        try {
            return createViewGroup(root, refs, data);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to create View Hierarchy", e);
        }
    }

    private static ViewGroup createViewGroup(Instance instance, RefHolder refs, DumpData data) throws IOException {
        ClassDefinition cls = data.classes.get(instance.getClassObjectId());
         instance.getObjectField(refs.childrenField, data.classes);
        return null;
    }

    private static ClassDefinition findClassByName(String name, DumpData data) {
        for (ClassDefinition cls : data.classes.values()) {
            if (name.equals(data.strings.get(cls.getNameStringId()).getValue())) {
                return cls;
            }
        }
        throw new IllegalArgumentException("No class with name " + name + " found!");
    }

    private static InstanceField findFieldByName(String name, BasicType type, ClassDefinition cls, DumpData data) {
        for (InstanceField field : cls.getInstanceFields()) {
            if (field.getType() == type && name.equals(data.strings.get(field.getFieldNameId()).getValue())) {
                return field;
            }
        }
        throw new IllegalArgumentException("Field " + name + " not found!");
    }
}
