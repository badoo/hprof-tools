package com.badoo.hprof.viewer.factory;

/**
 * Model class which defines the environment on which the dump was created
 * <p/>
 * Created by Erik Andre on 08/12/15.
 */
public class Environment {

    public final int sdkVersion;

    public Environment(int sdkVersion) {
        this.sdkVersion = sdkVersion;
    }
}
