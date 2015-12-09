package com.badoo.hprof.viewer.factory;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.library.model.PrimitiveArray;
import com.badoo.hprof.viewer.MemoryDump;
import com.badoo.hprof.viewer.factory.classdefs.StringClassDef;
import com.google.common.primitives.Chars;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Factory for creating String from String instance dumps
 * <p/>
 * Created by Erik Andre on 08/12/15.
 */
public class StringFactory extends BaseClassFactory<StringClassDef, String> {

    private static StringFactory instance;

    public static StringFactory getInstance(@Nonnull MemoryDump data, @Nonnull Environment env) {
        if (instance == null) {
            instance = new StringFactory(data, env);
        }
        return instance;
    }

    private StringFactory(@Nonnull MemoryDump data, @Nonnull Environment env) {
        super(data, env);
    }

    @Override
    protected String create(@Nonnull Instance instance, @Nonnull MemoryDump data, @Nonnull Environment env, @Nonnull StringClassDef classDef) throws IOException {
        final int valueObjectId = instance.getObjectField(classDef.value, data.classes);
        final int offset = classDef.offset != null ? instance.getIntField(classDef.offset, data.classes) : 0;
        final int count = instance.getIntField(classDef.count, data.classes);
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

    @Nonnull
    @Override
    protected StringClassDef createClassDef(@Nonnull MemoryDump data) {
        return new StringClassDef(data);
    }
}
