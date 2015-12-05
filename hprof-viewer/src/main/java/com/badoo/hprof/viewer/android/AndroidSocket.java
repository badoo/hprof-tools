package com.badoo.hprof.viewer.android;

/**
 * Model class for an Android socket
 *
 * Created by Erik Andre on 05/12/15.
 */
public class AndroidSocket {

    private final String host;
    private final String hostIp;
    private final boolean isConnected;
    private final boolean isClosed;
    private final int port;

    public AndroidSocket(String host, String hostIp, int port, boolean isConnected, boolean isClosed) {
        this.host = host;
        this.hostIp = hostIp;
        this.port = port;
        this.isConnected = isConnected;
        this.isClosed = isClosed;
    }

    public String getHost() {
        return host;
    }

    public String getHostIp() {
        return hostIp;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public int getPort() {
        return port;
    }
}
