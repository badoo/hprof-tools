package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Base class for instance dump class definitions
 *
 * Created by Erik Andre on 05/12/15.
 */
public class BaseClassDef {

    @Nonnull
    protected static ClassDefinition findClassByName(@Nonnull String name, @Nonnull MemoryDump data) {
        ClassDefinition cls = tryFindClassByName(name, data);
        if (cls == null) {
            throw new IllegalArgumentException("No class with name " + name + " found!");
        }
        return cls;
    }

    @Nullable
    protected static ClassDefinition tryFindClassByName(@Nonnull String name, @Nonnull MemoryDump data) {
        for (ClassDefinition cls : data.classes.values()) {
            if (name.equals(data.strings.get(cls.getNameStringId()).getValue())) {
                return cls;
            }
        }
        return null;
    }

    @Nonnull
    protected static InstanceField findFieldByName(String name, BasicType type, ClassDefinition cls, MemoryDump data) {
        for (InstanceField field : cls.getInstanceFields()) {
            if (field.getType() == type && name.equals(data.strings.get(field.getFieldNameId()).getValue())) {
                return field;
            }
        }
        // Error reporting
        StringBuilder error = new StringBuilder();
        for (InstanceField field : cls.getInstanceFields()) {
            error.append(data.strings.get(field.getFieldNameId())).append(" ").append(field.getType()).append("\n");
        }
        throw new IllegalArgumentException("Field " + name + " not found in " + data.strings.get(cls.getNameStringId()) + "\n" + error.toString());
    }
}
