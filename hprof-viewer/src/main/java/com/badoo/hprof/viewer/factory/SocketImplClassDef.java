package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;

import javax.annotation.Nonnull;

/**
 * Class definition for a SocketImpl instance
 *
 * Created by Erik Andre on 05/12/15.
 */
public class SocketImplClassDef extends BaseClassDef {

    final ClassDefinition cls;
    final InstanceField address;
    final InstanceField port;

     public SocketImplClassDef(@Nonnull DumpData data) {
            cls = findClassByName("java.net.SocketImpl", data);
            address = findFieldByName("address", BasicType.OBJECT, cls, data);
            port = findFieldByName("port", BasicType.INT, cls, data);
    }

}
