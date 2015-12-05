package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of a Location
 *
 * Created by Erik Andre on 05/12/15.
 */
public class LocationClassDef extends BaseClassDef {

    final ClassDefinition cls;
    final InstanceField provider;
    final InstanceField latitude;
    final InstanceField longitude;
    final InstanceField accuracy;
    final InstanceField time;

    public LocationClassDef(@Nonnull DumpData data) {
        this.cls = findClassByName("android.location.Location", data);;
        this.provider = findFieldByName("mProvider", BasicType.OBJECT, cls, data);
        this.latitude = findFieldByName("mLatitude", BasicType.DOUBLE, cls, data);
        this.longitude = findFieldByName("mLongitude", BasicType.DOUBLE, cls, data);
        this.accuracy = findFieldByName("mAccuracy", BasicType.FLOAT, cls, data);
        this.time = findFieldByName("mTime", BasicType.LONG, cls, data);
    }
}
