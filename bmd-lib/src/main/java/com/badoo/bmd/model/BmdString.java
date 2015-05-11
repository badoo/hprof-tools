package com.badoo.bmd.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class containing a BMD string. Consists of a string id and either a string value or a hash code.
 * <p/>
 * Created by Erik Andre on 23/10/14.
 */
public class BmdString {

    private final String string;
    private final int hash;
    private final int id;
    private final int length;

    public BmdString(int id, @Nonnull String string) {
        this.string = string;
        this.hash = string.hashCode();
        this.length = string.getBytes().length;
        this.id = id;
    }

    public BmdString(int id, int length, int hash) {
        this.string = null;
        this.hash = hash;
        this.length = length;
        this.id = id;
    }

    /**
     * Returns the string data or null if this BmdString only has a hash.
     *
     * @return The string data or null if there is none
     */
    @Nullable
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

    /**
     * Returns the length of the string in bytes. If this is a hashed string then the returned value will
     * the length of the string before it was hashed.
     *
     * @return The length of the string in bytes
     */
    public int getLength() {
        return length;
    }
}
