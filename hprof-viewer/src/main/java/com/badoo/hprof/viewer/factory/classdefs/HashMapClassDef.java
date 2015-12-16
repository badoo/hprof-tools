package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;




/**
 * Class definition for creating HashMaps
 *
 * Created by Erik Andre on 09/12/15.
 */
public class HashMapClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField table;
    public final HashEntryClassDef hashEntryClassDef;

    public HashMapClassDef(@Nonnull MemoryDump data) {
        cls = data.findClassByName("java.util.HashMap");
        table = data.findFieldByName("table", BasicType.OBJECT, cls);
        hashEntryClassDef = new HashEntryClassDef(data);
    }

    public static class HashEntryClassDef extends BaseClassDef {

        public final ClassDefinition cls;
        public final InstanceField key;
        public final InstanceField value;
        public final InstanceField next;

        public HashEntryClassDef(@Nonnull MemoryDump data) {
            cls = data.findClassByName("java.util.HashMap$HashMapEntry");
            key = data.findFieldByName("key", BasicType.OBJECT, cls);
            value = data.findFieldByName("value", BasicType.OBJECT, cls);
            next = data.findFieldByName("next", BasicType.OBJECT, cls);
        }
    }
}
