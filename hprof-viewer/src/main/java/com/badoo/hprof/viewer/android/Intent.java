package com.badoo.hprof.viewer.android;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Model class representing an Android Intent instance
 *
 * Created by Erik Andre on 05/12/15.
 */
public class Intent {

    private final String action;
    private final Bundle extras;

    public Intent(@Nullable String action, @Nonnull Bundle extras) {
        this.action = action;
        this.extras = extras;
    }

    @Nullable
    public String getAction() {
        return action;
    }

    @Nonnull
    public Bundle getExtras() {
        return extras;
    }
}
