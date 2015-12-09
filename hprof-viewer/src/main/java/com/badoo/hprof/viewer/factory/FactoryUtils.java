package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.MemoryDump;

/**
 * Collection of utility methods for creating model instances based on instance dump data
 * <p/>
 * Created by Erik Andre on 08/12/15.
 */
public class FactoryUtils {

    protected static String getClassName(Instance instance, MemoryDump data) {
        ClassDefinition cls = data.classes.get(instance.getClassObjectId());
        return data.strings.get(cls.getNameStringId()).getValue();
    }

    protected static boolean isInstanceOf(Instance instance, ClassDefinition of, MemoryDump data) {
        if (instance == null || of == null) {
            return false;
        }
        ClassDefinition cls = data.classes.get(instance.getClassObjectId());
        while (cls != null) {
            if (cls == of) {
                return true;
            }
            cls = data.classes.get(cls.getSuperClassObjectId());
        }
        return false;
    }
}
