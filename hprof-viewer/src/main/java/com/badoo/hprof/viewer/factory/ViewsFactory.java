package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.android.View;
import com.badoo.hprof.viewer.factory.classdefs.ViewsClassDef;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Factory class for creating any type of Views (ImageViews, TextViews, regular Views, etc)
 *
 * Created by Erik Andre on 08/12/15.
 */
public class ViewsFactory extends BaseClassFactory<ViewsClassDef, View> {

    private static ViewsFactory instance;

    public static ViewsFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        if (instance == null) {
            instance = new ViewsFactory(data, env);
        }
        return instance;
    }

    public ViewsFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
    }

    @Override
    protected View create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull ViewsClassDef classDef) throws IOException {
        if (FactoryUtils.isInstanceOf(instance, classDef.textView, data)) {
            return TextViewFactory.getInstance(data, env).create(instance);
        }
        else if (FactoryUtils.isInstanceOf(instance, classDef.imageView, data)) {
            return ImageViewFactory.getInstance(data, env).create(instance);
        }
        else if (FactoryUtils.isInstanceOf(instance, classDef.viewGroup, data)) {
            return ViewGroupFactory.getInstance(data, env).create(instance);
        }
        return GenericViewFactory.getInstance(data, env).create(instance);
    }

    @Nonnull
    @Override
    protected ViewsClassDef createClassDef(@Nonnull MemoryDump data) {
        return new ViewsClassDef(data);
    }
}
