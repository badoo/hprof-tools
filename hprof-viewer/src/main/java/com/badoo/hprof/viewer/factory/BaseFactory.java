package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.model.ObjectArray;
import com.badoo.hprof.library.model.PrimitiveArray;
import com.badoo.hprof.viewer.DumpData;
import com.google.common.primitives.Chars;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for factories dealing with dump data
 * <p/>
 * Created by Erik Andre on 05/12/15.
 */
public class BaseFactory {
    protected static String getClassName(Instance instance, DumpData data) {
        ClassDefinition cls = data.classes.get(instance.getClassObjectId());
        return data.strings.get(cls.getNameStringId()).getValue();
    }

    protected static String readString(Instance instance, BaseRefHolder refs, DumpData data) throws IOException {
        ClassDefinition cls = data.classes.get(instance.getClassObjectId());
        if (cls == refs.string.cls) {
            final int valueObjectId = instance.getObjectField(refs.string.value, data.classes);
            final int offset = instance.getIntField(refs.string.offset, data.classes);
            final int count = instance.getIntField(refs.string.count, data.classes);
            PrimitiveArray value = data.primitiveArrays.get(valueObjectId);
            if (value.getType() != BasicType.CHAR) {
                throw new IllegalArgumentException("String.value field is not of type char[]");
            }
            StringBuilder builder = new StringBuilder();
            byte[] bytes = value.getArrayData();
            for (int i = 0; i < bytes.length; i += 2) {
                builder.append(Chars.fromBytes(bytes[i], bytes[i + 1]));
            }
            return builder.toString().substring(offset, offset + count);
        }
        return data.strings.get(cls.getNameStringId()).getValue();
    }

    protected static Map<String, String> createMap(Instance map, BaseRefHolder refs, DumpData data) throws IOException {
        if (!isInstanceOf(map, refs.arrayMap.cls, data)) {
            System.err.println("Unsupported map: " + data.strings.get(data.classes.get(map.getClassObjectId()).getNameStringId()).getValue());
            return null;
        }
        int size = map.getIntField(refs.arrayMap.size, data.classes);
        ObjectArray array = data.objArrays.get(map.getObjectField(refs.arrayMap.array, data.classes));
        if (array == null) {
            return null;
        }
        Map<String, String> values = new HashMap<String, String>();
        for (int i = 0; i < size; i++) {
            String key = instanceToString(data.instances.get(array.getElements()[i * 2]), refs, data);
            String value = instanceToString(data.instances.get(array.getElements()[i * 2 + 1]), refs, data);
            values.put(key, value);
        }
        return values;
    }

    private static String instanceToString(Instance instance, BaseRefHolder refs, DumpData data) throws IOException {
        if (isInstanceOf(instance, refs.string.cls, data)) {
            return readString(instance, refs, data);
        }
        else if (isInstanceOf(instance, refs.bool.cls, data)) {
            return Boolean.toString(instance.getBooleanField(refs.bool.value, data.classes));
        }
        else if (isInstanceOf(instance, refs.integer.cls, data)) {
            return Integer.toString(instance.getIntField(refs.integer.value, data.classes));
        }
        else if (isInstanceOf(instance, refs.enumDef.cls, data)) {
            String clsName = getClassName(instance, data);
            Instance nameInstance = data.instances.get(instance.getObjectField(refs.enumDef.name, data.classes));
            String name = readString(nameInstance, refs, data);
            int ordinal = instance.getIntField(refs.enumDef.ordinal, data.classes);
            return clsName + "." + name + " (" + ordinal + ")";
        }
        return data.strings.get(data.classes.get(instance.getClassObjectId()).getNameStringId()).getValue();
    }

    protected static boolean isInstanceOf(Instance instance, ClassDefinition of, DumpData data) {
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
