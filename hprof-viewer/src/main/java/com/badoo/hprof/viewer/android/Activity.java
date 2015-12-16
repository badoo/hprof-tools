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
    private final Intent intent;

    public Activity(@Nonnull String name, @Nullable String title, @Nullable Intent intent) {
        this.name = name;
        this.title = title;
        this.intent = intent;
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

    /**
     * Returns the Intent used to start this activity
     *
     * @return the starting intent
     */
    @Nullable
    public Intent getIntent() {
        return intent;
    }

    @Override
    public String toString() {
        return name + (title != null? " (" + title + ")" : "");
    }
}
