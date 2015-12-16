package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of a ColorDrawable
 * <p/>
 * Created by Erik Andre on 05/12/15.
 */
public class LollipopColorDrawableClassDef extends ColorDrawableClassDef {

    public LollipopColorDrawableClassDef(@Nonnull MemoryDump data) {
        super(data, "mColorState", "mBaseColor");
    }
}
