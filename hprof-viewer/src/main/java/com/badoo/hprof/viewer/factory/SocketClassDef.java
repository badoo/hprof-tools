package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;

import javax.annotation.Nonnull;

/**
 * Class definition for an Android socket
 *
 * Created by Erik Andre on 05/12/15.
 */
public class SocketClassDef extends BaseClassDef {

    final ClassDefinition cls;
    final InstanceField impl;
    final InstanceField isConnected;
    final InstanceField isClosed;

    public SocketClassDef(@Nonnull DumpData data) {
        cls = findClassByName("java.net.Socket", data);
        impl = findFieldByName("impl", BasicType.OBJECT, cls, data);
        isConnected = findFieldByName("isConnected", BasicType.BOOLEAN, cls, data);
        isClosed = findFieldByName("isClosed", BasicType.BOOLEAN, cls, data);
    }
}
