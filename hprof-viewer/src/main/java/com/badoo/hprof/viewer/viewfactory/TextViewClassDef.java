package com.badoo.hprof.viewer.viewfactory;

import com.badoo.hprof.library.model.BasicType;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.viewer.DumpData;

import javax.annotation.Nonnull;

/**
 * Class definition for accessing data of an instance dump of a TextView
 *
 * Created by Erik Andre on 05/12/15.
 */
class TextViewClassDef extends BaseClassDef {

    final ClassDefinition cls;
    final InstanceField text;

    public TextViewClassDef(@Nonnull DumpData data) {
        cls = findClassByName("android.widget.TextView", data);
        text = findFieldByName("mText", BasicType.OBJECT, cls, data);
    }
}
