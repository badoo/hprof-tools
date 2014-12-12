package com.badoo.bmd;

import java.io.IOException;

/**
 * Created by Erik Andre on 02/11/14.
 */
public interface BmdProcessor {

    public void onRecord(int tag, BmdReader reader) throws IOException;

    public void onHeader(int version, byte[] data) throws IOException;
}
