package com.badoo.hprof.viewer.android;

/**
 * Model representing an enum (name changed to avoid confusion and name clashes)
 *
 * Created by Erik Andre on 08/12/15.
 */
public class AndroidEnum {

    private final String name;
    private final String value;
    private final int ordinal;

    public AndroidEnum(String name, String value, int ordinal) {
        this.name = name;
        this.value = value;
        this.ordinal = ordinal;
    }

    public String getName() {
        return name;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name + "." + value + " (" + ordinal + ")";
    }
}
