package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.viewer.android.AndroidSocket;
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
    private final List<AndroidSocket> sockets;

    public SystemInfo(@Nonnull List<Location> locations, @Nonnull List<AndroidSocket> sockets) {
        this.locations = locations;
        this.sockets = sockets;
    }

    @Nonnull
    public List<Location> getLocations() {
        return locations;
    }

    public List<AndroidSocket> getSockets() {
        return sockets;
    }
}
