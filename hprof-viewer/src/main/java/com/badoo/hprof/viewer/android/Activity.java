package com.badoo.hprof.viewer.android;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Model class for Android activities
 * <p/>
 * Created by Erik Andre on 05/12/15.
 */
public class Activity {

    private final String name;
    private final String title;

    public Activity(@Nonnull String name, @Nullable String title) {
        this.name = name;
        this.title = title;
    }

    /**
     * Returns the (class) name of the activity
     *
     * @return the class name of the activity
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * Returns the (toolbar) title of the activity
     *
     * @return the title of the activity
     */
    @Nullable
    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return name + (title != null? " (" + title + ")" : "");
    }
}
