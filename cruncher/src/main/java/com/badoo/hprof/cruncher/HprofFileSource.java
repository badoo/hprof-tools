package com.badoo.hprof.cruncher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

/**
 * HPROF data source backed by a File
 * <p/>
 * Created by Erik Andre on 11/05/15.
 */
public class HprofFileSource implements HprofSource {

    private final File file;

    public HprofFileSource(File file) {
        this.file = file;
    }

    @Nonnull
    @Override
    public InputStream open() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public long getDataSize() {
        return file.length();
    }
}
