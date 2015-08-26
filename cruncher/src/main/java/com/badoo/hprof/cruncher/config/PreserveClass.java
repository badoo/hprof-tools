package com.badoo.hprof.cruncher.config;

import javax.annotation.Nonnull;

/**
* Created by Erik Andre on 26/08/15.
*/
public class PreserveClass {

    private final String className;

    public PreserveClass(@Nonnull String className) {
        this.className = className;
    }

    @Nonnull
    public String getClassToPreserve() {
        return className;
    }
}
