package com.badoo.hprof.viewer.viewfactory;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;

/**
 * Base class for instance dump class definitions
 *
 * Created by Erik Andre on 05/12/15.
 */
class BaseClassDef {

    protected ClassDefinition findClassByName(String name, DumpData data) {
        for (ClassDefinition cls : data.classes.values()) {
            if (name.equals(data.strings.get(cls.getNameStringId()).getValue())) {
                return cls;
            }
        }
        throw new IllegalArgumentException("No class with name " + name + " found!");
    }

    protected InstanceField findFieldByName(String name, BasicType type, ClassDefinition cls, DumpData data) {
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
