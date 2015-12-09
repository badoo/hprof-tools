package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.android.View;
import com.badoo.hprof.viewer.factory.classdefs.GenericViewClassDef;

import java.io.IOException;

import javax.annotation.Nonnull;

import static com.badoo.hprof.viewer.factory.FactoryUtils.getClassName;

/**
 * Factory for creating generic Views (those not handled by more specific factories like TextViewFactory)
 *
 * Created by Erik Andre on 08/12/15.
 */
public class GenericViewFactory extends BaseClassFactory<GenericViewClassDef, View> {

    private static GenericViewFactory sInstance;

    public static GenericViewFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        // Only one implementation of this factory
        if (sInstance == null) {
            sInstance = new GenericViewFactory(data, env);
        }
        return sInstance;
    }

    private GenericViewFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data,env);
    }

    @Override
    protected View create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull GenericViewClassDef classDef) throws IOException {
        int flags = instance.getIntField(classDef.flags, data.classes);
        int left = instance.getIntField(classDef.left, data.classes);
        int right = instance.getIntField(classDef.right, data.classes);
        int top = instance.getIntField(classDef.top, data.classes);
        int bottom = instance.getIntField(classDef.bottom, data.classes);
        View view = new View(getClassName(instance, data),left, right, top, bottom, flags);
        view.setBackground(DrawableFactory.getInstance(data, env).create(data.instances.get(instance.getObjectField(classDef.background, data.classes))));
        return view;
    }

    @Nonnull
    @Override
    protected GenericViewClassDef createClassDef(@Nonnull MemoryDump data) {
        return new GenericViewClassDef(data);
    }
}
