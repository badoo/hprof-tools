package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of a TextView
 *
 * Created by Erik Andre on 05/12/15.
 */
public class TextViewClassDef extends GenericViewClassDef {

    public final ClassDefinition textViewCls;
    public final InstanceField text;

    public TextViewClassDef(@Nonnull MemoryDump data) {
        super(data);
        textViewCls = data.findClassByName("android.widget.TextView");
        text = data.findFieldByName("mText", BasicType.OBJECT, textViewCls);
    }
}
