package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.library.model.StaticField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility methods for handling class data
 *
 * Created by Erik Andre on 09/12/15.
 */
public class ClassUtils {

    @Nonnull
    public static ClassDefinition findClassByName(@Nonnull String name, @Nonnull MemoryDump data) {
        ClassDefinition cls = tryFindClassByName(name, data);
        if (cls == null) {
            throw new IllegalArgumentException("No class with name " + name + " found!");
        }
        return cls;
    }

    @Nullable
    public static ClassDefinition tryFindClassByName(@Nonnull String name, @Nonnull MemoryDump data) {
        for (ClassDefinition cls : data.classes.values()) {
            if (name.equals(data.strings.get(cls.getNameStringId()).getValue())) {
                return cls;
            }
        }
        return null;
    }

    @Nonnull
    public static InstanceField findFieldByName(@Nonnull String name, @Nonnull BasicType type, @Nonnull ClassDefinition cls, @Nonnull MemoryDump data) {
        InstanceField foundField = tryFindFieldByName(name, type, cls, data);
        if (foundField == null) {
            // Error reporting
            StringBuilder error = new StringBuilder();
            for (InstanceField field : cls.getInstanceFields()) {
                error.append(data.strings.get(field.getFieldNameId())).append(" ").append(field.getType()).append("\n");
            }
            throw new IllegalArgumentException("Field " + name + " not found in " + data.strings.get(cls.getNameStringId()) + "\n" + error.toString());
        }
        return foundField;
    }

    @Nullable
    public static InstanceField tryFindFieldByName(@Nonnull String name, @Nonnull BasicType type, @Nonnull ClassDefinition cls, @Nonnull MemoryDump data) {
        for (InstanceField field : cls.getInstanceFields()) {
            if (field.getType() == type && name.equals(data.strings.get(field.getFieldNameId()).getValue())) {
                return field;
            }
        }
        return null;
    }

    @Nonnull
    public static StaticField findStaticFieldByName(@Nonnull String name, @Nonnull BasicType type, @Nonnull ClassDefinition cls, @Nonnull MemoryDump data) {
        for (StaticField field : cls.getStaticFields()) {
            String fieldName = data.strings.get(field.getFieldNameId()).getValue();
            if (field.getType() == type && name.equals(fieldName)) {
                return field;
            }
        }
        // Error reporting
        StringBuilder error = new StringBuilder();
        for (StaticField field : cls.getStaticFields()) {
            error.append(data.strings.get(field.getFieldNameId())).append(" ").append(field.getType()).append("\n");
        }
        throw new IllegalArgumentException("Static field " + name + " not found in " + getClassName(cls, data));
    }

    public static String getClassName(@Nonnull Instance instance, @Nonnull MemoryDump data) {
        ClassDefinition cls = data.classes.get(instance.getClassObjectId());
        return data.strings.get(cls.getNameStringId()).getValue();
    }

    public static String getClassName(@Nonnull ClassDefinition cls, MemoryDump data) {
        return data.strings.get(cls.getNameStringId()).getValue();
    }

    public static boolean isInstanceOf(Instance instance, ClassDefinition of, MemoryDump data) {
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
