package com.badoo.hprof.unobfuscator;

import com.badoo.hprof.library.HprofReader;
import proguard.obfuscate.MappingProcessor;
import proguard.obfuscate.MappingReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Erik Andre on 13/08/2014.
 */
public class HprofUnobfuscator implements MappingProcessor {

    public static void main(String args[]) {
        new HprofUnobfuscator("/Users/erikandre/temp/hprof/mapping.txt", "/Users/erikandre/temp/hprof/obfuscated.hprof", "/Users/erikandre/temp/hprof/out.hprof");
    }

    public HprofUnobfuscator(String mappingFile, String hprofFile, String outFile) {
        MappingReader mappingReader = new MappingReader(new File(mappingFile));
        try {
            mappingReader.pump(this);
            UnobfuscatingProcessor unobfuscatingProcessor = new UnobfuscatingProcessor(new FileOutputStream(outFile));
            HprofReader hprofReader = new HprofReader(new FileInputStream(hprofFile), unobfuscatingProcessor);
            while (hprofReader.hasNext()) {
                hprofReader.next();
            }
        } catch (IOException e) {
            System.err.println("Failed to convert hprof file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean processClassMapping(String className, String newClassName) {
//        System.out.println("Class mapping " + className + "->" + newClassName);
        return true;
    }

    @Override
    public void processFieldMapping(String className, String fieldType, String fieldName, String newFieldName) {
//        System.out.println("Field mapping " + className + "." + fieldName + "->" + newFieldName);
    }

    @Override
    public void processMethodMapping(String className, int firstLineNumber, int lastLineNumber, String methodReturnType, String methodName, String methodArguments, String newMethodName) {
//        System.out.println("Method mapping " + className + "." + methodName + "->" + newMethodName);
    }

}
