package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;

import static com.badoo.hprof.library.model.BasicType.*;


/**
 * Class definition for a SocketImpl instance
 *
 * Created by Erik Andre on 05/12/15.
 */
public class SocketImplClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField address;
    public final InstanceField port;

     public SocketImplClassDef(@Nonnull MemoryDump data) {
            cls = data.findClassByName("java.net.SocketImpl");
            address = data.findFieldByName("address", OBJECT, cls);
            port = data.findFieldByName("port", INT, cls);
    }

}
