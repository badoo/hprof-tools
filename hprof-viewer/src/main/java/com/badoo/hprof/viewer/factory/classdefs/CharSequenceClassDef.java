package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;



/**
 * Class definition used to process instance dumps for classes implementing CharSequence.
 * <p/>
 * Created by Erik Andre on 09/12/15.
 */
public class CharSequenceClassDef extends BaseClassDef {

    public final ClassDefinition string;

    public CharSequenceClassDef(@Nonnull MemoryDump data) {
        string = data.findClassByName("java.lang.String");
    }
}
