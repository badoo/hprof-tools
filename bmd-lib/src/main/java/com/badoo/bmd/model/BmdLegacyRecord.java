package com.badoo.bmd.model;

/**
 * Created by Erik Andre on 09/11/14.
 */
public class BmdLegacyRecord {

    private final int originalTag;
    private final byte[] data;

    public BmdLegacyRecord(int originalTag, byte[] data) {
        this.originalTag = originalTag;
        this.data = data;
    }

    public int getOriginalTag() {
        return originalTag;
    }

    public byte[] getData() {
        return data;
    }
}
