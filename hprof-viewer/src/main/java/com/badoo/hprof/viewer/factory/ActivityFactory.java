package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.android.Activity;
import com.badoo.hprof.viewer.android.Intent;
import com.badoo.hprof.viewer.factory.classdefs.ActivityClassDef;
import com.badoo.hprof.viewer.factory.classdefs.ClassUtils;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Factory class for creating Activities from instance dump data
 * <p/>
 * Created by Erik Andre on 09/12/15.
 */
public class ActivityFactory extends BaseClassFactory<ActivityClassDef, Activity> {

    private static ActivityFactory instance;

    public static ActivityFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        if (instance == null) {
            instance = new ActivityFactory(data, env);
        }
        return instance;
    }

    private ActivityFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
    }

    @Override
    protected Activity create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull ActivityClassDef classDef) throws IOException {
        // Name and title
        String className = ClassUtils.getClassName(instance, data);
//        Instance titleInstance = data.instances.get(instance.getObjectField(classDef.title, data.classes));
        Instance titleInstance = null;
        String title = StringFactory.getInstance(data, env).create(titleInstance);
        // Intent
//        Instance intentInstance = data.instances.get(instance.getObjectField(classDef.intent, data.classes));
        Instance intentInstance = null;
        Intent intent = IntentFactory.getInstance(data, env).create(intentInstance);
        return new Activity(className, title, intent);
    }

    @Nonnull
    @Override
    protected ActivityClassDef createClassDef(@Nonnull MemoryDump data) {
        return new ActivityClassDef(data);
    }
}
