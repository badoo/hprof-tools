package com.badoo.hprof.library.model;

import javax.annotation.Nonnull;

/**
 * Class containing the data associated with a hprof STRING_RECORD.
 *
 * Created by Erik Andre on 15/08/2014.
 */
public class HprofString extends Record {

    private int id;
    private String value;

    public HprofString(int id, @Nonnull String value, int timestamp) {
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
    @Nonnull
    public String getValue() {
        return value;
    }

    public void setValue(@Nonnull String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value + " (" + id + ")";
    }
}
