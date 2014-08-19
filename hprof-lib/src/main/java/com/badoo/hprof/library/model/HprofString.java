package com.badoo.hprof.library.model;

/**
 * Created by Erik Andre on 15/08/2014.
 */
public class HprofString extends Record {

    private int id;
    private String value;


    public HprofString(int id, java.lang.String value, int timestamp) {
        this.id = id;
        this.value = value;
        setTimestamp(timestamp);
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

    @Override
    public String toString() {
        return value + " (" + id + ")";
    }
}
