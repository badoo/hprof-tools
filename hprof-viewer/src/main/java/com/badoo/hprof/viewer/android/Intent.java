package com.badoo.hprof.viewer.android;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Model class representing an Android Intent instance
 *
 * Created by Erik Andre on 05/12/15.
 */
public class Intent {

    private final String action;
    private final Map<String, String> extras;

    public Intent(@Nullable String action, Map<String, String> extras) {
        this.action = action;
        this.extras = extras;
    }

    @Nullable
    public String getAction() {
        return action;
    }

    public Map<String, String> getExtras() {
        return extras;
    }
}
