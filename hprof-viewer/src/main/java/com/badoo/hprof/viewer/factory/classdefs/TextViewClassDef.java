package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;
import com.badoo.hprof.viewer.factory.classdefs.BaseClassDef;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of a TextView
 *
 * Created by Erik Andre on 05/12/15.
 */
public class TextViewClassDef extends BaseClassDef {

    public final ClassDefinition cls;
    public final InstanceField text;

    public TextViewClassDef(@Nonnull DumpData data) {
        cls = findClassByName("android.widget.TextView", data);
        text = findFieldByName("mText", BasicType.OBJECT, cls, data);
    }
}
