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
        cls = data.findClassByName("java.net.Socket");
        impl = data.findFieldByName("impl", BasicType.OBJECT, cls);
        isConnected = data.findFieldByName("isConnected", BasicType.BOOLEAN, cls);
        isClosed = data.findFieldByName("isClosed", BasicType.BOOLEAN, cls);
    }
}
