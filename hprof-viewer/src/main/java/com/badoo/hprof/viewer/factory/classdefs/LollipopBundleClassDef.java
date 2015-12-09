package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of an Intent
 *
 * Created by Erik Andre on 05/12/15.
 */
public class LollipopBundleClassDef extends BundleClassDef {

    public LollipopBundleClassDef(@Nonnull MemoryDump data) {
        super(findClassByName("android.os.BaseBundle", data), data);
    }

}
