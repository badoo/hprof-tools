package com.badoo.hprof.library.model;

/**
 * Base class for data types that can be read from (or written to) hprof records.
 * <p/>
 * Created by Erik Andre on 15/08/2014.
 */
public class Record {

    private int timestamp;

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
