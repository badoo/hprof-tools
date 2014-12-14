package com.badoo.bmd.model;

/**
 * Class containing a BMD string. Consists of a string id and either a string value or a hash code.
 * <p/>
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

    /**
     * Returns the string data or null if this BmdString only has a hash.
     *
     * @return The string data or null if there is none
     */
    public String getString() {
        return string;
    }

    /**
     * Returns the string hash if it's defined.
     *
     * @return The string hash
     */
    public int getHash() {
        return hash;
    }

    /**
     * Returns the string id which globally identifies it.
     *
     * @return The string id
     */
    public int getId() {
        return id;
    }
}
