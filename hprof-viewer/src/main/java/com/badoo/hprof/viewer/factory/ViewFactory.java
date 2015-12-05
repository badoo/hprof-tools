package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.model.ObjectArray;
import com.badoo.hprof.library.model.PrimitiveArray;
import com.badoo.hprof.viewer.BitmapFactory;
import com.badoo.hprof.viewer.DumpData;
import com.badoo.hprof.viewer.android.Activity;
import com.badoo.hprof.viewer.android.Drawable;
import com.badoo.hprof.viewer.android.ImageView;
import com.badoo.hprof.viewer.android.Intent;
import com.badoo.hprof.viewer.android.TextView;
import com.badoo.hprof.viewer.android.View;
import com.badoo.hprof.viewer.android.ViewGroup;
import com.badoo.hprof.viewer.android.drawables.BitmapDrawable;
import com.badoo.hprof.viewer.android.drawables.ColorDrawable;
import com.badoo.hprof.viewer.android.drawables.EmptyDrawable;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Factory class for creating Views from Instances and ClassDefinitions.
 */
public class ViewFactory extends BaseFactory{

    /**
     * Class holding references to the class definitions we need to decode a view hierarchy
     */
    private static class ViewRefHolder extends BaseRefHolder {

        // Views
        final ViewClassDef view;
        final ViewGroupClassDef viewGroup;
        final TextViewClassDef textView;
        final ImageViewClassDef imageView;

        // Drawables
        final ColorDrawableClassDef colorDrawable;
        final BitmapDrawableClassDef bitmapDrawable;

        // Other classes
        final BitmapClassDef bitmap;

        private ViewRefHolder(DumpData data) {
            super(data);
            view = new ViewClassDef(data);
            viewGroup = new ViewGroupClassDef(data);
            textView = new TextViewClassDef(data);
            colorDrawable = new ColorDrawableClassDef(data);
            bitmapDrawable = new BitmapDrawableClassDef(data);
            bitmap = new BitmapClassDef(data);
            imageView = new ImageViewClassDef(data);
        }
    }

    /**
     * Build the View hierarchy starting at a specified root ViewGroup
     *
     * @param root the root ViewGroup instance
     * @param data all the data of the associated memory dump
     * @return a ViewGroup linked to the other Views in this hierarchy
     */
    @Nonnull
    public static Screen buildViewHierarchy(@Nonnull Instance root, @Nonnull DumpData data) {
        ViewRefHolder refs = new ViewRefHolder(data);
        try {
            Activity activity = createActivity(root, refs, data);
            return new Screen(createViewGroup(root, refs, data), activity);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to create View Hierarchy", e);
        }
    }

    private static Activity createActivity(Instance root, ViewRefHolder refs, DumpData data) throws IOException {
        Instance activity = data.instances.get(root.getObjectField(refs.view.context, data.classes));
        if (activity != null) {
            // Name and title
            ClassDefinition activityClass = data.classes.get(activity.getClassObjectId());
            final HprofString name = data.strings.get(activityClass.getNameStringId());
            int titleInstanceId = activity.getObjectField(refs.activity.title, data.classes);
            String titleString = null;
            if (titleInstanceId != 0) {
                Instance title = data.instances.get(titleInstanceId);
                titleString = readString(title, refs, data);
            }
            // Intent
            Intent restoredIntent = null;
            Instance intent = data.instances.get(activity.getObjectField(refs.activity.intent, data.classes));
            if (intent != null) {
                String actionString = null;
                Instance action = data.instances.get(intent.getObjectField(refs.intent.action, data.classes));
                if (action != null) {
                    actionString = readString(action, refs, data);
                }
                Instance bundle = data.instances.get(intent.getObjectField(refs.intent.extras, data.classes));
                if (bundle != null) {
                    Instance map = data.instances.get(bundle.getObjectField(refs.bundle.map, data.classes));
                    if (map != null) {
                        Map<String, String> extras = createMap(map, refs, data);
                        restoredIntent = new Intent(actionString, extras);
                    }
                }
            }
            return new Activity(name.getValue(), titleString, restoredIntent);
        }
        return null;
    }

    private static ViewGroup createViewGroup(Instance instance, ViewRefHolder refs, DumpData data) throws IOException {
        int childFieldId = instance.getObjectField(refs.viewGroup.children, data.classes);
        List<View> children = new ArrayList<View>();
        if (childFieldId != 0) {
            ObjectArray childArray = data.objArrays.get(childFieldId);
            for (int element : childArray.getElements()) {
                if (element == 0) {
                    // Null value in child array, can probably be ignored?
                    continue;
                }
                Instance childInstance = data.instances.get(element);
                if (isInstanceOf(childInstance, refs.viewGroup.cls, data)) {
                    children.add(createViewGroup(childInstance, refs, data));
                }
                else {
                    children.add(createView(childInstance, refs, data));
                }
            }
        }
        int backgroundId = instance.getObjectField(refs.view.background, data.classes);
        Drawable background = createDrawable(backgroundId, refs, data);
        int flags = instance.getIntField(refs.view.flags, data.classes);
        int left = instance.getIntField(refs.view.left, data.classes);
        int right = instance.getIntField(refs.view.right, data.classes);
        int top = instance.getIntField(refs.view.top, data.classes);
        int bottom = instance.getIntField(refs.view.bottom, data.classes);
        ViewGroup group = new ViewGroup(getClassName(instance, data), children, left, right, top, bottom, flags);
        group.setBackground(background);
        return group;
    }

    private static View createView(Instance instance, ViewRefHolder refs, DumpData data) throws IOException {
        int backgroundId = instance.getObjectField(refs.view.background, data.classes);
        Drawable background = createDrawable(backgroundId, refs, data);
        int flags = instance.getIntField(refs.view.flags, data.classes);
        int left = instance.getIntField(refs.view.left, data.classes);
        int right = instance.getIntField(refs.view.right, data.classes);
        int top = instance.getIntField(refs.view.top, data.classes);
        int bottom = instance.getIntField(refs.view.bottom, data.classes);
        final String className = getClassName(instance, data);

        View view = null;
        if (isInstanceOf(instance, refs.textView.cls, data)) { // TextView
            int textObjId = instance.getObjectField(refs.textView.text, data.classes);
            Instance textInstance = data.instances.get(textObjId);
            // The text field is an object which implements the CharSequence interface. How to access the actual text
            // is dependant on which implementation of the interface we are dealing with.
            String text = readString(textInstance, refs, data);
            view = new TextView(className, left, right, top, bottom, flags, text);
        }
        else if (isInstanceOf(instance, refs.imageView.cls, data)) { // ImageView
            int drawableId = instance.getObjectField(refs.imageView.drawable, data.classes);
            Drawable drawable = createDrawable(drawableId, refs, data);
            view = new ImageView(className, left, right, top, bottom, flags, drawable);

        }
        if (view == null) {
            view = new View(className, left, right, top, bottom, flags);
        }
        view.setBackground(background);
        return view;
    }

    private static Drawable createDrawable(int drawableId, ViewRefHolder refs, DumpData data) throws IOException {
        if (drawableId == 0) {
            return new EmptyDrawable();
        }
        Instance instance = data.instances.get(drawableId);
        if (isInstanceOf(instance, refs.bitmapDrawable.cls, data)) {
            int stateId = instance.getObjectField(refs.bitmapDrawable.stateField, data.classes);
            Instance state = data.instances.get(stateId);
            int bitmapId = state.getObjectField(refs.bitmapDrawable.state.bitmap, data.classes);
            if (bitmapId != 0) {
                Instance bitmap = data.instances.get(bitmapId);
                int bufferId = bitmap.getObjectField(refs.bitmap.buffer, data.classes);
                int width = bitmap.getIntField(refs.bitmap.width, data.classes);
                int height = bitmap.getIntField(refs.bitmap.height, data.classes);
                if (bufferId != 0) {
                    PrimitiveArray buffer = data.primitiveArrays.get(bufferId);
                    BufferedImage image = BitmapFactory.createBitmap(bitmapId, buffer.getArrayData(), width, height);
                    return new BitmapDrawable(image);
                }
            }
        }
        if (isInstanceOf(instance, refs.colorDrawable.cls, data)) {
            int stateFieldId = instance.getObjectField(refs.colorDrawable.stateField, data.classes);
            if (stateFieldId != 0) {
                Instance colorState = data.instances.get(stateFieldId);
                int color = colorState.getIntField(refs.colorDrawable.state.baseColor, data.classes);
                return new ColorDrawable(color);
            }
        }
        return new EmptyDrawable();
    }

}
