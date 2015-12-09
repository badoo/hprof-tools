package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.StaticField;
import com.badoo.hprof.library.util.StreamUtil;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.factory.classdefs.ClassUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Model class which defines the environment on which the dump was created
 * <p/>
 * Created by Erik Andre on 08/12/15.
 */
public class Environment {

    public final int sdkVersion;

    public Environment(MemoryDump data) {
        ClassDefinition versionCls = ClassUtils.findClassByName("android.os.Build$VERSION", data);
        StaticField versionField = ClassUtils.findStaticFieldByName("SDK_INT", BasicType.INT, versionCls, data);
        try {
            sdkVersion = StreamUtil.readInt(new ByteArrayInputStream(versionField.getValue()));
            System.out.println("Dump was created on Android version " + sdkVersion);

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
