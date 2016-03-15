package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.model.ObjectArray;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.android.View;
import com.badoo.hprof.viewer.android.ViewGroup;
import com.badoo.hprof.viewer.factory.classdefs.ViewGroupClassDef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;



/**
 * Factory for creating ViewsGroups
 *
 * Created by Erik Andre on 08/12/15.
 */
public class ViewGroupFactory extends BaseClassFactory<ViewGroupClassDef, ViewGroup> {

    private static ViewGroupFactory instance;

    private ViewGroupClassDef cls;

    public static ViewGroupFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        // Only one implementation of this factory
        if (instance == null) {
            instance = new ViewGroupFactory(data, env);
        }
        return instance;
    }

    private ViewGroupFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
    }

    @Override
    protected ViewGroup create(@Nonnull Instance instance, @Nonnull MemoryDump data, Environment env, @Nonnull ViewGroupClassDef classDef) throws IOException {
        List<View> children = new ArrayList<View>();
        ObjectArray childArray = data.objArrays.get(instance.getObjectField(classDef.children, data.classes));
        final int childrenCount = instance.getIntField(classDef.childrenCount, data.classes);
        if (childArray != null) {
            for (int i = 0; i < childrenCount; i++) {
                View child = ViewsFactory.getInstance(data, env).create(data.instances.get(childArray.getElements()[i]));
                if (child != null) {
                    children.add(child);
                }
            }
        }
        int flags = instance.getIntField(classDef.flags, data.classes);
        int left = instance.getIntField(classDef.left, data.classes);
        int right = instance.getIntField(classDef.right, data.classes);
        int top = instance.getIntField(classDef.top, data.classes);
        int bottom = instance.getIntField(classDef.bottom, data.classes);
        ViewGroup group = new ViewGroup(instance, data.getClassName(instance), children, left, right, top, bottom, flags);
        group.setBackground(DrawableFactory.getInstance(data, env).create(data.instances.get(instance.getObjectField(classDef.background, data.classes))));
        return group;
    }

    @Nonnull
    @Override
    protected ViewGroupClassDef createClassDef(@Nonnull MemoryDump data) {
        return new ViewGroupClassDef(data);
    }

}
