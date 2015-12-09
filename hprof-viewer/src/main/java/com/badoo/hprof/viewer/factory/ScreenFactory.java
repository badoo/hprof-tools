package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.android.Activity;
import com.badoo.hprof.viewer.android.ViewGroup;
import com.badoo.hprof.viewer.factory.classdefs.GenericViewClassDef;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Factory class for creating Screens with their View Hierarchies and activity information from Instances and ClassDefinitions.
 */
public class ScreenFactory {

    /**
     * Build the View hierarchy starting at a specified root ViewGroup
     *
     * @param root the root ViewGroup instance
     * @param data all the data of the associated memory dump
     * @return a ViewGroup linked to the other Views in this hierarchy
     */
    @Nonnull
    public static Screen buildViewHierarchy(@Nonnull Instance root, @Nonnull MemoryDump data, @Nonnull Environment env) {
        try {
            ViewGroup viewRoot = ViewGroupFactory.getInstance(data, env).create(root);
            Activity activity = createActivity(root, data, env);
            return new Screen(viewRoot, activity);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to create View Hierarchy", e);
        }
    }

    private static Activity createActivity(Instance root, MemoryDump data, Environment env) throws IOException {
        GenericViewClassDef classDef = new GenericViewClassDef(data);
        Instance activityInstance = data.instances.get(root.getObjectField(classDef.context, data.classes));
        return ActivityFactory.getInstance(data, env).create(activityInstance);
    }

}
