package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;
import static com.badoo.hprof.viewer.factory.classdefs.ClassUtils.findClassByName;
import static com.badoo.hprof.viewer.factory.classdefs.ClassUtils.findFieldByName;

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
        this.cls = findClassByName("android.location.Location", data);;
        this.provider = findFieldByName("mProvider", BasicType.OBJECT, cls, data);
        this.latitude = findFieldByName("mLatitude", BasicType.DOUBLE, cls, data);
        this.longitude = findFieldByName("mLongitude", BasicType.DOUBLE, cls, data);
        this.accuracy = findFieldByName("mAccuracy", BasicType.FLOAT, cls, data);
        this.time = findFieldByName("mTime", BasicType.LONG, cls, data);
    }
}
