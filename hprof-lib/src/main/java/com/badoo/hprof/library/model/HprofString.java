package com.badoo.hprof.library.model;

/**
 * Created by Erik Andre on 15/08/2014.
 */
public class HprofString {

    private int id;
    private String value;
    private int timestamp;


    public HprofString(int id, java.lang.String value, int timestamp) {
        this.id = id;
        this.value = value;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public java.lang.String getValue() {
        return value;
    }

    public void setValue(java.lang.String value) {
        this.value = value;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
