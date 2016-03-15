package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.android.Drawable;
import com.badoo.hprof.viewer.android.ImageView;
import com.badoo.hprof.viewer.factory.classdefs.ImageViewClassDef;

import java.io.IOException;

import javax.annotation.Nonnull;



/**
 * Factory for TextViews from instance dump data
 *
 * Created by Erik Andre on 08/12/15.
 */
public class ImageViewFactory extends BaseClassFactory<ImageViewClassDef, ImageView> {

    private static ImageViewFactory sInstance;

    public static ImageViewFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        // Only one implementation of this factory
        if (sInstance == null) {
            sInstance = new ImageViewFactory(data, env);
        }
        return sInstance;
    }

    private ImageViewFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data,env);
    }

    @Override
    protected ImageView create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull ImageViewClassDef classDef) throws IOException {
        int flags = instance.getIntField(classDef.flags, data.classes);
        int left = instance.getIntField(classDef.left, data.classes);
        int right = instance.getIntField(classDef.right, data.classes);
        int top = instance.getIntField(classDef.top, data.classes);
        int bottom = instance.getIntField(classDef.bottom, data.classes);
        Instance imageInstance = data.instances.get(instance.getObjectField(classDef.drawable, data.classes));
        Drawable image = DrawableFactory.getInstance(data, env).create(imageInstance);
        ImageView view = new ImageView(instance, data.getClassName(instance),left, right, top, bottom, flags, image);
        view.setBackground(DrawableFactory.getInstance(data, env).create(data.instances.get(instance.getObjectField(classDef.background, data.classes))));
        return view;
    }

    @Nonnull
    @Override
    protected ImageViewClassDef createClassDef(@Nonnull MemoryDump data) {
        return new ImageViewClassDef(data);
    }
}
