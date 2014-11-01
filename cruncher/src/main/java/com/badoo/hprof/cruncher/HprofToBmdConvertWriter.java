package com.badoo.hprof.cruncher;

import com.badoo.hprof.cruncher.bmd.BmdTag;
import com.badoo.hprof.cruncher.bmd.DataWriter;
import com.badoo.hprof.cruncher.bmd.model.BmdClassDefinition;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.model.Instance;
import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class for writing HPROF data converted to the more compact BMD format.
 *
 * Created by Erik Andre on 25/10/14.
 */
public class HprofToBmdConvertWriter extends DataWriter {

    protected HprofToBmdConvertWriter(OutputStream out) {
        super(out);
    }

}
