package com.badoo.hprof.viewer;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.library.model.ObjectArray;
import com.badoo.hprof.library.model.PrimitiveArray;
import com.badoo.hprof.viewer.model.TextView;
import com.badoo.hprof.viewer.model.View;
import com.badoo.hprof.viewer.model.ViewGroup;
import com.google.common.primitives.Chars;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class ViewFactory {

    private static class RefHolder {

        final ClassDefinition viewClass;
        final ClassDefinition viewGroupClass;
        final ClassDefinition textViewClass;
        final ClassDefinition stringClass;
        final ClassDefinition colorDrawableClass;
        final ClassDefinition colorStateClass;

        final InstanceField viewGroupChildrenField;
        final InstanceField viewLeftField;
        final InstanceField viewRightField;
        final InstanceField viewTopField;
        final InstanceField viewBottomField;
        final InstanceField viewFlagsField;
        final InstanceField viewBackgroundField;

        final InstanceField textViewTextField;

        final InstanceField stringValueField;
        final InstanceField stringOffsetField;
        final InstanceField stringCountField;

        final InstanceField colorDrawableColorStateField;
        final InstanceField colorStateBaseColorField;

        private RefHolder(DumpData data) {
            // Classes
            viewClass = findClassByName("android.view.View", data);
            viewGroupClass = findClassByName("android.view.ViewGroup", data);
            textViewClass = findClassByName("android.widget.TextView", data);
            stringClass = findClassByName("java.lang.String", data);
            colorDrawableClass = findClassByName("android.graphics.drawable.ColorDrawable", data);
            colorStateClass = findClassByName("android.graphics.drawable.ColorDrawable$ColorState", data);

            // Fields
            viewGroupChildrenField = findFieldByName("mChildren", BasicType.OBJECT, viewGroupClass, data);

            viewLeftField = findFieldByName("mLeft", BasicType.INT, viewClass, data);
            viewRightField = findFieldByName("mRight", BasicType.INT, viewClass, data);
            viewTopField = findFieldByName("mTop", BasicType.INT, viewClass, data);
            viewBottomField = findFieldByName("mBottom", BasicType.INT, viewClass, data);
            viewFlagsField = findFieldByName("mViewFlags", BasicType.INT, viewClass, data);
            viewBackgroundField = findFieldByName("mBackground", BasicType.OBJECT, viewClass, data);

            textViewTextField = findFieldByName("mText", BasicType.OBJECT, textViewClass, data);

            stringValueField = findFieldByName("value", BasicType.OBJECT, stringClass, data);
            stringOffsetField = findFieldByName("offset", BasicType.INT, stringClass, data);
            stringCountField = findFieldByName("count", BasicType.INT, stringClass, data);

            colorDrawableColorStateField = findFieldByName("mColorState", BasicType.OBJECT, colorDrawableClass, data);
            colorStateBaseColorField = findFieldByName("mBaseColor", BasicType.INT, colorStateClass, data);
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
        int childFieldId = instance.getObjectField(refs.viewGroupChildrenField, data.classes);
        List<View> children = new ArrayList<View>();
        if (childFieldId != 0) {
            ObjectArray childArray = data.objArrays.get(childFieldId);
            for (int element : childArray.getElements()) {
                if (element == 0) {
                    // Null value in child array, can probably be ignored?
                    continue;
                }
                Instance childInstance = data.instances.get(element);
                if (isInstanceOf(childInstance, refs.viewGroupClass, data)) {
                    children.add(createViewGroup(childInstance, refs, data));
                }
                else {
                    children.add(createView(childInstance, refs, data));
                }
            }
        }
        // Try to parse the background
        int backgroundId = instance.getObjectField(refs.viewBackgroundField, data.classes);
        int backgroundColor = 0;
        if (backgroundId != 0) {
            backgroundColor = getDrawableColor(data.instances.get(backgroundId), refs, data);
        }

        int flags = instance.getIntField(refs.viewFlagsField, data.classes);
        int left = instance.getIntField(refs.viewLeftField, data.classes);
        int right = instance.getIntField(refs.viewRightField, data.classes);
        int top = instance.getIntField(refs.viewTopField, data.classes);
        int bottom = instance.getIntField(refs.viewBottomField, data.classes);
        return new ViewGroup(children, left, right, top, bottom, getClassName(instance, data), flags, backgroundColor);
    }

    private static View createView(Instance instance, RefHolder refs, DumpData data) throws IOException {
        // Try to parse the background
        int backgroundId = instance.getObjectField(refs.viewBackgroundField, data.classes);
        int backgroundColor = 0;
        if (backgroundId != 0) {
            backgroundColor = getDrawableColor(data.instances.get(backgroundId), refs, data);
        }

        int flags = instance.getIntField(refs.viewFlagsField, data.classes);
        int left = instance.getIntField(refs.viewLeftField, data.classes);
        int right = instance.getIntField(refs.viewRightField, data.classes);
        int top = instance.getIntField(refs.viewTopField, data.classes);
        int bottom = instance.getIntField(refs.viewBottomField, data.classes);
        if (isInstanceOf(instance, refs.textViewClass, data)) { // TextView
            int textObjId = instance.getObjectField(refs.textViewTextField, data.classes);
            Instance textInstance = data.instances.get(textObjId);
            // The text field is an object which implements the CharSequence interface. How to access the actual text
            // is dependant on which implementation of the interface we are dealing with.
            String text = getTextFromCharSequence(textInstance, refs, data);
            if (text.length() > 100) {
                System.out.println("Long text: " + textObjId + ", cls=" + getClassName(textInstance, data) + ", val=" + text);
            }
            return new TextView(text, left, right, top, bottom, flags, backgroundColor);
        }
        else {
            return new View(left, right, top, bottom, getClassName(instance, data), flags, backgroundColor);
        }
    }

    private static String getClassName(Instance instance, DumpData data) {
        ClassDefinition cls = data.classes.get(instance.getClassObjectId());
        return data.strings.get(cls.getNameStringId()).getValue();
    }

    private static int getDrawableColor(Instance instance, RefHolder refs, DumpData data) throws IOException {
        if (!"android.graphics.drawable.ColorDrawable".equals(getClassName(instance, data))) {
            System.out.print("Unsupported background: " + getClassName(instance, data));
            return 0x00000000;
        }
        int stateFieldId = instance.getObjectField(refs.colorDrawableColorStateField, data.classes);
        if (stateFieldId != 0) {
            Instance colorState = data.instances.get(stateFieldId);
            return colorState.getIntField(refs.colorStateBaseColorField, data.classes);
        }
        return 0xffff0000; // Just to catch errors!
    }

    private static String getTextFromCharSequence(Instance instance, RefHolder refs, DumpData data) throws IOException {
        ClassDefinition cls = data.classes.get(instance.getClassObjectId());
        if (cls == refs.stringClass) {
            final int valueObjectId = instance.getObjectField(refs.stringValueField, data.classes);
            final int offset = instance.getIntField(refs.stringOffsetField, data.classes);
            final int count = instance.getIntField(refs.stringCountField, data.classes);
            PrimitiveArray value = data.primitiveArrays.get(valueObjectId);
            if (value.getType() != BasicType.CHAR) {
                throw new IllegalArgumentException("String.value field is not of type char[]");
            }
            StringBuilder builder = new StringBuilder();
            byte[] bytes = value.getArrayData();
            for (int i = 0; i < bytes.length; i += 2) {
                builder.append(Chars.fromBytes(bytes[i], bytes[i + 1]));
            }
            return builder.toString().substring(offset, offset + count);
        }
        return data.strings.get(cls.getNameStringId()).getValue();
    }

    private static boolean isInstanceOf(Instance childInstance, ClassDefinition of, DumpData data) {
        ClassDefinition cls = data.classes.get(childInstance.getClassObjectId());
        while (cls != null) {
            if (cls == of) {
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
        // Error reporting
        StringBuilder error = new StringBuilder();
        for (InstanceField field : cls.getInstanceFields()) {
            error.append(data.strings.get(field.getFieldNameId())).append(" ").append(field.getType()).append("\n");
        }
        throw new IllegalArgumentException("Field " + name + " not found in " + data.strings.get(cls.getNameStringId()) + "\n" + error.toString());
    }
}
