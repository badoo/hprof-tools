package com.badoo.hprof.viewer.viewfactory;

import com.badoo.hprof.viewer.android.Activity;
import com.badoo.hprof.viewer.android.ViewGroup;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Model class for a screen containing a View Hierarchy and the activity it is attached to.
 *
 * Created by Erik Andre on 05/12/15.
 */
public class Screen {

    private final ViewGroup viewRoot;
    private final Activity activity;

    public Screen(@Nonnull ViewGroup viewRoot, @Nullable Activity activity) {
        this.viewRoot = viewRoot;
        this.activity = activity;
    }

    @Nonnull
    public ViewGroup getViewRoot() {
        return viewRoot;
    }

    @Nullable
    public Activity getActivity() {
        return activity;
    }

    @Override
    public String toString() {
        if (activity != null) {
            return activity.toString();
        }
        return viewRoot.toString();
    }
}
