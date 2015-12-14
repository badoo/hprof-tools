package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;



/**
 * Class definition for accessing data of an instance dump of a Location
 *
 * Created by Erik Andre on 05/12/15.
 */
public class LocationClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField provider;
    public final InstanceField latitude;
    public final InstanceField longitude;
    public final InstanceField accuracy;
    public final InstanceField time;

    public LocationClassDef(@Nonnull MemoryDump data) {
        this.cls = data.findClassByName("android.location.Location");;
        this.provider = data.findFieldByName("mProvider", BasicType.OBJECT, cls);
        this.latitude = data.findFieldByName("mLatitude", BasicType.DOUBLE, cls);
        this.longitude = data.findFieldByName("mLongitude", BasicType.DOUBLE, cls);
        this.accuracy = data.findFieldByName("mAccuracy", BasicType.FLOAT, cls);
        this.time = data.findFieldByName("mTime", BasicType.LONG, cls);
    }
}
