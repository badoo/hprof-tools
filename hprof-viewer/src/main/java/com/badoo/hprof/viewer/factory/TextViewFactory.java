package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.android.TextView;
import com.badoo.hprof.viewer.factory.classdefs.TextViewClassDef;

import java.io.IOException;

import javax.annotation.Nonnull;



/**
 * Factory for ImageViews from instance dump data
 *
 * Created by Erik Andre on 08/12/15.
 */
public class TextViewFactory extends BaseClassFactory<TextViewClassDef, TextView> {

    private static TextViewFactory sInstance;

    public static TextViewFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        // Only one implementation of this factory
        if (sInstance == null) {
            sInstance = new TextViewFactory(data, env);
        }
        return sInstance;
    }

    private TextViewFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data,env);
    }

    @Override
    protected TextView create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull TextViewClassDef classDef) throws IOException {
        int flags = instance.getIntField(classDef.flags, data.classes);
        int left = instance.getIntField(classDef.left, data.classes);
        int right = instance.getIntField(classDef.right, data.classes);
        int top = instance.getIntField(classDef.top, data.classes);
        int bottom = instance.getIntField(classDef.bottom, data.classes);
        Instance textInstance = data.instances.get(instance.getObjectField(classDef.text, data.classes));
        CharSequence text = CharSequenceFactory.getInstance(data, env).create(textInstance);
        TextView view = new TextView(instance, data.getClassName(instance),left, right, top, bottom, flags, text);
        view.setBackground(DrawableFactory.getInstance(data, env).create(data.instances.get(instance.getObjectField(classDef.background, data.classes))));
        return view;
    }

    @Nonnull
    @Override
    protected TextViewClassDef createClassDef(@Nonnull MemoryDump data) {
        return new TextViewClassDef(data);
    }
}
