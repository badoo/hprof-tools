package com.badoo.hprof.validator;


import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class ValidatorTest {

    @Test
    public void validate_out_file() throws Exception {
        final File srcFile = new File("../test_files/decrunch_test_out.hprof");
        final String absolutePath = srcFile.getAbsolutePath();
        HprofValidator.main(new String[]{absolutePath});
    }

    @Test
    public void validate_x86() throws Exception {
        //todo why is this one red ?
        final File srcFile = new File("../test_files/x86_obfuscated.hprof");
        final String absolutePath = srcFile.getAbsolutePath();
        HprofValidator.main(new String[]{absolutePath});
    }


}
