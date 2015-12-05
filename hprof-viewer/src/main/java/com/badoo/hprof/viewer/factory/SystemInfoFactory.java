package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.DumpData;
import com.badoo.hprof.viewer.android.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Factory class for creating system information classes based on memory dump data.
 * <p/>
 * Created by Erik Andre on 05/12/15.
 */
public class SystemInfoFactory extends BaseFactory {

    private static class SystemRefHolder extends BaseRefHolder {

        final LocationClassDef location;

        public SystemRefHolder(@Nonnull DumpData data) {
            super(data);
            location = new LocationClassDef(data);
        }
    }

    public static SystemInfo createSystemInfo(@Nonnull DumpData data) {
        // Find location data
        SystemRefHolder refs = new SystemRefHolder(data);
        List<Location> locations = null;
        try {
            locations = createLocations(refs, data);
        }
        catch (IOException e) {
            System.err.println("Failed to read system info, " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return new SystemInfo(locations);
    }

    private static List<Location> createLocations(SystemRefHolder refs, DumpData data) throws IOException {
        List<Location> result = new ArrayList<Location>();
        for (Instance instance : data.instances.values()) {
            if (isInstanceOf(instance, refs.location.cls, data)) {
                // Provider name
                Instance provider = data.instances.get(instance.getObjectField(refs.location.provider, data.classes));
                String providerName = readString(provider, refs, data);
                // Latitude
                double latitude = instance.getDoubleField(refs.location.latitude, data.classes);
                // Longitude
                double longitude = instance.getDoubleField(refs.location.longitude, data.classes);
                // Accuracy
                float accuracy = instance.getFloatField(refs.location.accuracy, data.classes);
                // Timestamp
                long time = instance.getLongField(refs.location.time, data.classes);
                result.add(new Location(providerName, latitude, longitude, accuracy, time));
            }
        }
        return result;
    }
}
