package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.viewer.android.Location;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class containing various system information
 *
 * Created by Erik Andre on 05/12/15.
 */
public class SystemInfo {

    private final List<Location> locations;

    public SystemInfo(@Nullable List<Location> locations) {
        this.locations = locations != null? locations : Collections.<Location>emptyList();
    }

    @Nonnull
    public List<Location> getLocations() {
        return locations;
    }
}
