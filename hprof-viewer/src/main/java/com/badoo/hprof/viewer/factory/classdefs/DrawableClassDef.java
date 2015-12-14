package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;


/**
 * Class definition for accessing data of an instance dump of a Drawable
 *
 * Created by Erik Andre on 08/12/15.
 */
public class DrawableClassDef extends BaseClassDef {

    public final ClassDefinition colorDrawableCls;
    public final ClassDefinition bitmapDrawableCls;

    public DrawableClassDef(@Nonnull MemoryDump data) {
        colorDrawableCls = data.findClassByName("android.graphics.drawable.ColorDrawable");
        bitmapDrawableCls = data.findClassByName("android.graphics.drawable.BitmapDrawable");
    }
}
