package com.badoo.hprof.viewer.ui.instances;

import com.badoo.hprof.library.model.Instance;

import javax.annotation.Nonnull;

/**
 * Created by Erik Andre on 13/12/15.
 */
public class InstanceInfo {

    public final Instance instance;
    public final String name;
    public final int size;

    public InstanceInfo(@Nonnull Instance instance, @Nonnull String name, int size) {
        this.instance = instance;
        this.name = name;
        this.size = size;
    }

    @Override
    public String toString() {
        return name + " @ " + instance.getObjectId();
    }
}
