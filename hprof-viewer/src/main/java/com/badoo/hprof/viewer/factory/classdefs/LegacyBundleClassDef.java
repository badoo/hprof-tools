package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;


/**
 * Class definition for Bundle on Android versions older than 5.0
 *
 * Created by Erik Andre on 08/12/15.
 */
public class LegacyBundleClassDef extends BundleClassDef {

    public LegacyBundleClassDef(@Nonnull MemoryDump data) {
        super(data.findClassByName("android.os.Bundle"), data);
    }
}
