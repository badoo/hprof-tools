package com.badoo.hprof.viewer;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.library.model.ObjectArray;
import com.badoo.hprof.library.model.PrimitiveArray;
import com.badoo.hprof.viewer.model.ImageView;
import com.badoo.hprof.viewer.model.TextView;
import com.badoo.hprof.viewer.model.View;
import com.badoo.hprof.viewer.model.ViewGroup;
import com.google.common.primitives.Chars;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for creating Views from Instances and ClassDefinitions.
 */
public class ViewFactory {

    private static class RefHolder {

        // View
        final ClassDefinition viewClass;
        final InstanceField viewLeftField;
        final InstanceField viewRightField;
        final InstanceField viewTopField;
        final InstanceField viewBottomField;
        final InstanceField viewFlagsField;
        final InstanceField viewBackgroundField;

        // ViewGroup
        final ClassDefinition viewGroupClass;
        final InstanceField viewGroupChildrenField;

        // TextView
        final ClassDefinition textViewClass;
        final InstanceField textViewTextField;

        // String
        final ClassDefinition stringClass;
        final InstanceField stringValueField;
        final InstanceField stringOffsetField;
        final InstanceField stringCountField;

        // ColorDrawable
        final ClassDefinition colorDrawableClass;
        final ClassDefinition colorStateClass;
        final InstanceField colorDrawable_colorStateField;
        final InstanceField colorState_baseColorField;

        // BitmapDrawable
        final ClassDefinition bitmapDrawableClass;
        final ClassDefinition bitmapStateClass;
        final InstanceField bitmapDrawable_bitmapStateField;
        final InstanceField bitmapState_bitmapField;

        // Bitmap
        final ClassDefinition bitmapClass;
        final InstanceField bitmap_buffer;
        final InstanceField bitmap_width;
        final InstanceField bitmap_height;

        // ImageView
        final ClassDefinition imageViewClass;
        final InstanceField imageView_drawable;

        private RefHolder(DumpData data) {

            // View
            viewClass = findClassByName("android.view.View", data);
            viewLeftField = findFieldByName("mLeft", BasicType.INT, viewClass, data);
            viewRightField = findFieldByName("mRight", BasicType.INT, viewClass, data);
            viewTopField = findFieldByName("mTop", BasicType.INT, viewClass, data);
            viewBottomField = findFieldByName("mBottom", BasicType.INT, viewClass, data);
            viewFlagsField = findFieldByName("mViewFlags", BasicType.INT, viewClass, data);
            viewBackgroundField = findFieldByName("mBackground", BasicType.OBJECT, viewClass, data);

            // ViewGroup
            viewGroupClass = findClassByName("android.view.ViewGroup", data);
            viewGroupChildrenField = findFieldByName("mChildren", BasicType.OBJECT, viewGroupClass, data);

            // TextView
            textViewClass = findClassByName("android.widget.TextView", data);
            textViewTextField = findFieldByName("mText", BasicType.OBJECT, textViewClass, data);

            // String
            stringClass = findClassByName("java.lang.String", data);
            stringValueField = findFieldByName("value", BasicType.OBJECT, stringClass, data);
            stringOffsetField = findFieldByName("offset", BasicType.INT, stringClass, data);
            stringCountField = findFieldByName("count", BasicType.INT, stringClass, data);

            // ColorDrawable
            colorDrawableClass = findClassByName("android.graphics.drawable.ColorDrawable", data);
            colorDrawable_colorStateField = findFieldByName("mColorState", BasicType.OBJECT, colorDrawableClass, data);
            colorStateClass = findClassByName("android.graphics.drawable.ColorDrawable$ColorState", data);
            colorState_baseColorField = findFieldByName("mBaseColor", BasicType.INT, colorStateClass, data);

            // BitmapDrawable
            bitmapDrawableClass = findClassByName("android.graphics.drawable.BitmapDrawable", data);
            bitmapDrawable_bitmapStateField = findFieldByName("mBitmapState", BasicType.OBJECT, bitmapDrawableClass, data);
            bitmapStateClass = findClassByName("android.graphics.drawable.BitmapDrawable$BitmapState", data);
            bitmapState_bitmapField = findFieldByName("mBitmap", BasicType.OBJECT, bitmapStateClass, data);

            // Bitmap
            bitmapClass = findClassByName("android.graphics.Bitmap", data);
            bitmap_buffer = findFieldByName("mBuffer", BasicType.OBJECT, bitmapClass, data);
            bitmap_width = findFieldByName("mWidth", BasicType.INT, bitmapClass, data);
            bitmap_height = findFieldByName("mHeight", BasicType.INT, bitmapClass, data);

            // ImageView
            imageViewClass = findClassByName("android.widget.ImageView", data);
            imageView_drawable = findFieldByName("mDrawable", BasicType.OBJECT, imageViewClass, data);
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
        else if (isInstanceOf(instance, refs.imageViewClass, data)) {
            int drawableId = instance.getObjectField(refs.imageView_drawable, data.classes);
            if (drawableId != 0) {
                Instance drawable = data.instances.get(drawableId);
                if (isInstanceOf(drawable, refs.bitmapDrawableClass, data)) {
                    int stateId = drawable.getObjectField(refs.bitmapDrawable_bitmapStateField, data.classes);
                    Instance state = data.instances.get(stateId);
                    int bitmapId = state.getObjectField(refs.bitmapState_bitmapField, data.classes);
                    if (bitmapId != 0) {
                        Instance bitmap = data.instances.get(bitmapId);
                        int bufferId = bitmap.getObjectField(refs.bitmap_buffer, data.classes);
                        int width = bitmap.getIntField(refs.bitmap_width, data.classes);
                        int height = bitmap.getIntField(refs.bitmap_height, data.classes);
                        if (bufferId != 0) {
                            PrimitiveArray buffer = data.primitiveArrays.get(bufferId);
                            BufferedImage image = BitmapFactory.createBitmap(bitmapId, buffer.getArrayData(), width, height);
                            return new ImageView(left, right, top, bottom, getClassName(instance, data), flags, backgroundColor, image);
                        }
                    }
                }
            }
        }
        return new View(left, right, top, bottom, getClassName(instance, data), flags, backgroundColor);
    }

    private static String getClassName(Instance instance, DumpData data) {
        ClassDefinition cls = data.classes.get(instance.getClassObjectId());
        return data.strings.get(cls.getNameStringId()).getValue();
    }

    private static int getDrawableColor(Instance instance, RefHolder refs, DumpData data) throws IOException {
        if (!"android.graphics.drawable.ColorDrawable".equals(getClassName(instance, data))) {
            System.out.println("Unsupported background: " + getClassName(instance, data));
            return 0x00000000;
        }
        int stateFieldId = instance.getObjectField(refs.colorDrawable_colorStateField, data.classes);
        if (stateFieldId != 0) {
            Instance colorState = data.instances.get(stateFieldId);
            return colorState.getIntField(refs.colorState_baseColorField, data.classes);
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
