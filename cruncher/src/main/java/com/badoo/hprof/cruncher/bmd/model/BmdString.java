package com.badoo.hprof.cruncher.bmd.model;

/**
 * Created by Erik Andre on 23/10/14.
 */
public class BmdString {

    private final String string;
    private final int hash;
    private final int id;

    public BmdString(int id, String string) {
        this.string = string;
        this.hash = string.hashCode();
        this.id = id;
    }

    public BmdString(int id, int hash) {
        this.string = null;
        this.hash = hash;
        this.id = id;
    }

    public String getString() {
        return string;
    }

    public int getHash() {
        return hash;
    }

    public int getId() {
        return id;
    }
}
