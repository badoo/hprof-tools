package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.viewer.DumpData;

import javax.annotation.Nonnull;

/**
 * Base class definition reference holder
 *
 * Created by Erik Andre on 05/12/15.
 */
public class BaseRefHolder {

    final StringClassDef string;
    final ActivityClassDef activity;
    final IntentClassDef intent;
    final BundleBaseClassDef bundle;
    final ArrayMapClassDef arrayMap;
    final BooleanClassDef bool;
    final IntegerClassDef integer;
    final EnumClassDef enumDef;

    public BaseRefHolder(@Nonnull DumpData data) {
        string = new StringClassDef(data);
        activity = new ActivityClassDef(data);
        intent = new IntentClassDef(data);
        bundle = new BundleBaseClassDef(data);
        arrayMap = new ArrayMapClassDef(data);
        bool = new BooleanClassDef(data);
        integer = new IntegerClassDef(data);
        enumDef = new EnumClassDef(data);
    }
}
