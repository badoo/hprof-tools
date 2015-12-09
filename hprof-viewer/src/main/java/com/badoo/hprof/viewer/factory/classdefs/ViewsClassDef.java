package com.badoo.hprof.viewer.factory.classdefs;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.viewer.MemoryDump;

import javax.annotation.Nonnull;
import static com.badoo.hprof.viewer.factory.classdefs.ClassUtils.findClassByName;
import static com.badoo.hprof.viewer.factory.classdefs.ClassUtils.findFieldByName;

/**
 * ClassDef containing a set of references to classes that can be used to determine which type of View an instance extends.
 *
 * Created by Erik Andre on 08/12/15.
 */
public class ViewsClassDef extends BaseClassDef {

    public final ClassDefinition textView;
    public final ClassDefinition imageView;
    public final ClassDefinition viewGroup;

    public ViewsClassDef(@Nonnull MemoryDump data) {
        textView = findClassByName("android.widget.TextView", data);
        imageView = findClassByName("android.widget.ImageView", data);
        viewGroup = findClassByName("android.view.ViewGroup", data);;
    }

}
