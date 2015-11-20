package com.badoo.hprof.viewer;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.library.model.ObjectArray;
import com.badoo.hprof.viewer.model.View;
import com.badoo.hprof.viewer.model.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class ViewFactory {

    private static class RefHolder {

        final ClassDefinition viewClass;
        final ClassDefinition viewGroupClass;
        final InstanceField childrenField;
        final InstanceField leftField;
        final InstanceField rightField;
        final InstanceField topField;
        final InstanceField bottomField;

        private RefHolder(DumpData data) {
            viewClass = findClassByName("android.view.View", data);
            viewGroupClass = findClassByName("android.view.ViewGroup", data);
            childrenField = findFieldByName("mChildren", BasicType.OBJECT, viewGroupClass, data);
            leftField = findFieldByName("mLeft", BasicType.INT, viewClass, data);
            rightField = findFieldByName("mRight", BasicType.INT, viewClass, data);
            topField = findFieldByName("mTop", BasicType.INT, viewClass, data);
            bottomField = findFieldByName("mBottom", BasicType.INT, viewClass, data);
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
        int childFieldId = instance.getObjectField(refs.childrenField, data.classes);
        List<View> children = new ArrayList<View>();
        if (childFieldId != 0) {
            ObjectArray childArray = data.objArrays.get(childFieldId);
            for (int element : childArray.getElements()) {
                if (element == 0) {
                    // Null value in child array, can probably be ignored?
                    continue;
                }
                Instance childInstance = data.instances.get(element);
                if (isViewGroup(childInstance, refs, data)) {
                    children.add(createViewGroup(childInstance, refs, data));
                }
                else {
                    children.add(createView(childInstance, refs, data));
                }
            }
        }
        int left = instance.getIntField(refs.leftField, data.classes);
        int right = instance.getIntField(refs.rightField, data.classes);
        int top = instance.getIntField(refs.topField, data.classes);
        int bottom = instance.getIntField(refs.bottomField, data.classes);
        return new ViewGroup(children, left, right, top, bottom);
    }

    private static View createView(Instance instance, RefHolder refs, DumpData data) throws IOException {
        int left = instance.getIntField(refs.leftField, data.classes);
        int right = instance.getIntField(refs.rightField, data.classes);
        int top = instance.getIntField(refs.topField, data.classes);
        int bottom = instance.getIntField(refs.bottomField, data.classes);
        return new View(left, right, top, bottom);
    }

    private static boolean isViewGroup(Instance childInstance, RefHolder refs, DumpData data) {
        ClassDefinition cls = data.classes.get(childInstance.getClassObjectId());
        while (cls != null) {
            if (cls == refs.viewGroupClass) {
                return true;
            }
            cls = data.classes.get(cls.getSuperClassObjectId());
        }
        return false;
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
