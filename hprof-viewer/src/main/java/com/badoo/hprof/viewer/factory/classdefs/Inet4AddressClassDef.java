package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;
import com.badoo.hprof.viewer.factory.classdefs.BaseClassDef;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of an Inet4Address
 *
 * Created by Erik Andre on 05/12/15.
 */
public class Inet4AddressClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField hostName;
    public final InstanceField ipaddress;

    public Inet4AddressClassDef(@Nonnull DumpData data) {
        cls = findClassByName("java.net.InetAddress", data);
        hostName = findFieldByName("hostName", BasicType.OBJECT, cls, data);
        ipaddress = findFieldByName("ipaddress", BasicType.OBJECT, cls, data);
    }
}
