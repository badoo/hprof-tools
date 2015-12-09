package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;

/**
 * Class definition for an Android socket
 *
 * Created by Erik Andre on 05/12/15.
 */
public class SocketClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField impl;
    public final InstanceField isConnected;
    public final InstanceField isClosed;

    public SocketClassDef(@Nonnull MemoryDump data) {
        cls = findClassByName("java.net.Socket", data);
        impl = findFieldByName("impl", BasicType.OBJECT, cls, data);
        isConnected = findFieldByName("isConnected", BasicType.BOOLEAN, cls, data);
        isClosed = findFieldByName("isClosed", BasicType.BOOLEAN, cls, data);
    }
}
