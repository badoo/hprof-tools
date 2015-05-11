package com.badoo.hprof.cruncher;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

/**
 * Interface for accessing HPROF files. Used instead of InputStream since we need to open the file multiple times (for the two processing passes).
 * <p/>
 * Created by Erik Andre on 11/05/15.
 */
public interface HprofSource {

    /**
     * Opens the HprofFile and returns an InputStream. If the method is called multiple times it should always return a new InputStream positioned at the start of the file.
     *
     * @return an InputStream connected to the HPROF file
     */
    @Nonnull
    InputStream open() throws IOException;

    /**
     * Returns the size the source HPROF file.
     *
     * @return the data size
     */
    long getDataSize();
}
