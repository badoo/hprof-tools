package com.badoo.hprof.deobfuscator;


import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.model.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

public class DeobfuscatorTest {

    private File tempFile;
    private DataCollectionProcessor processor;

    @Before
    public void setUp() throws Exception {
        tempFile = File.createTempFile("test", ".hprof");
        System.out.println(tempFile);
    }

    @Test
    public void deobfuscatex86() throws Exception {
        HprofDeobfuscator.main(new String[]{
                "../test_files/mapping.txt",
                "../test_files/x86_obfuscated.hprof",
                tempFile.getAbsolutePath()
        });

        processor = new DataCollectionProcessor();
        HprofReader reader = new HprofReader(new FileInputStream(tempFile), processor);
        while (reader.hasNext()) {
            reader.next();
        }

        final ClassDefinition fooBar  = findClassDefinition("example.com.hprof_tools_tmp_project.FooBar");
        Assert.assertNotNull(fooBar);

        final ClassDefinition mainActivity  = findClassDefinition("example.com.hprof_tools_tmp_project.MainActivity");
        Assert.assertNotNull(mainActivity);
        findField("instanceField", mainActivity.getInstanceFields());
        findField("staticField", mainActivity.getStaticFields());

    }

    @Test
    public void deobfuscatex64() throws Exception {
        HprofDeobfuscator.main(new String[]{
                "../test_files/mapping_x64.txt",
                "../test_files/x64_obfuscated.hprof",
                tempFile.getAbsolutePath()
        });

        processor = new DataCollectionProcessor();
        HprofReader reader = new HprofReader(new FileInputStream(tempFile), processor);
        while (reader.hasNext()) {
            reader.next();
        }

        final ClassDefinition main  = findClassDefinition("foo.bar.Main");
        Assert.assertNotNull(main);
        findField("instanceField", main.getInstanceFields());
        findField("staticField", main.getStaticFields());
    }

    private void findField(String strFieldName, List<? extends NamedField> fields) {
        final HprofString fieldName = findHprofString(strFieldName);
        for (NamedField field : fields) {
            if (field.getFieldNameId().equals(fieldName.getId())) {
                return ;
            }
        }
        throw new AssertionError("not found");
    }

    private ClassDefinition findClassDefinition( String qualifiedName) {
        HprofString value = findHprofString(qualifiedName);
        for (ClassDefinition cls : processor.getClasses().values()) {
            if (cls.getNameStringId().equals(value.getId())) {
                return cls;
            }
        }
        throw new AssertionError("not found");
    }

    private HprofString findHprofString(String qualifiedName) {
        for (Map.Entry<ID, HprofString> e : processor.getStrings().entrySet()) {
            HprofString value = e.getValue();
            if (value.getValue().equals(qualifiedName)) {
                return value;
            }
        }
        throw new AssertionError("not found");
    }

    @After
    public void tearDown() throws Exception {
        tempFile.delete();
    }

}
