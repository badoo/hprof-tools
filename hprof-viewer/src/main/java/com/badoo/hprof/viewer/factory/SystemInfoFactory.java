package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.model.PrimitiveArray;
import com.badoo.hprof.viewer.DumpData;
import com.badoo.hprof.viewer.android.AndroidSocket;
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
        final SocketClassDef socket;
        final SocketImplClassDef socketImpl;
        final Inet4AddressClassDef inet4Address;

        public SystemRefHolder(@Nonnull DumpData data) {
            super(data);
            location = new LocationClassDef(data);
            socket = new SocketClassDef(data);
            socketImpl = new SocketImplClassDef(data);
            inet4Address = new Inet4AddressClassDef(data);
        }
    }

    public static SystemInfo createSystemInfo(@Nonnull DumpData data) {
        try {
            SystemRefHolder refs = new SystemRefHolder(data);
            // Find location data
            List<Location> locations = null;
            locations = createLocations(refs, data);
            // Find socket information
            List<AndroidSocket> sockets = createSockets(refs, data);
            return new SystemInfo(locations, sockets);
        }
        catch (IOException e) {
            System.err.println("Failed to read system info, " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static List<AndroidSocket> createSockets(SystemRefHolder refs, DumpData data) throws IOException {
        List<AndroidSocket> result = new ArrayList<AndroidSocket>();
        for (Instance instance : data.instances.values()) {
            if (isInstanceOf(instance, refs.socket.cls, data)) {
                boolean isConnected = instance.getBooleanField(refs.socket.isConnected, data.classes);
                boolean isClosed = instance.getBooleanField(refs.socket.isClosed, data.classes);
                Instance impl = data.instances.get(instance.getObjectField(refs.socket.impl, data.classes));
                if (isInstanceOf(impl, refs.socketImpl.cls, data)) {
                    int port = impl.getIntField(refs.socketImpl.port, data.classes);
                    Instance address = data.instances.get(impl.getObjectField(refs.socketImpl.address, data.classes));
                    if (isInstanceOf(address, refs.inet4Address.cls, data)) {
                        String hostNameString = null;
                        Instance hostName = data.instances.get(address.getObjectField(refs.inet4Address.hostName, data.classes));
                        if (hostName != null) {
                            hostNameString = readString(hostName, refs, data);
                        }
                        String addressString = null;
                        PrimitiveArray rawAddress = data.primitiveArrays.get(address.getObjectField(refs.inet4Address.ipaddress, data.classes));
                        if (rawAddress != null) {
                            addressString = (rawAddress.getArrayData()[0] & 0xff) + "." + (rawAddress.getArrayData()[1] & 0xff)
                                + "." + (rawAddress.getArrayData()[2] & 0xff) + "." + (rawAddress.getArrayData()[3] & 0xff);
                        }
                        result.add(new AndroidSocket(hostNameString, addressString, port, isConnected, isClosed));
                    }
                }
            }
        }
        System.out.println("Found " + result.size() + " sockets");
        return result;
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
