package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.StaticField;
import com.badoo.hprof.library.util.StreamUtil;
import com.badoo.hprof.viewer.MemoryDump;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Model class which defines the environment on which the dump was created
 * <p/>
 * Created by Erik Andre on 08/12/15.
 */
public class Environment {

    private static Environment instance;

    public final int sdkVersion;

    @Nonnull
    public static Environment getEnvironment(@Nonnull MemoryDump data) {
        if (instance == null) {
            instance = new Environment(data);
        }
        return instance;
    }

    private Environment(@Nonnull MemoryDump data) {
        ClassDefinition versionCls = data.findClassByName("android.os.Build$VERSION");
        StaticField versionField = data.findStaticFieldByName("SDK_INT", BasicType.INT, versionCls);
        try {
            sdkVersion = StreamUtil.readInt(new ByteArrayInputStream(versionField.getValue()));
            System.out.println("Dump was created on Android version " + sdkVersion);

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
