package com.badoo.hprof.unobfuscator;

import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.StaticField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import proguard.obfuscate.MappingProcessor;
import proguard.obfuscate.MappingReader;

/**
 * Created by Erik Andre on 13/08/2014.
 */
public class HprofUnobfuscator implements MappingProcessor {

    private static class FieldInfo {

        final String className;

        final String fieldType;

        final String fieldName;

        final String obfuscatedFieldName;

        private FieldInfo(String className, String fieldType, String fieldName, String obfuscatedFieldName) {
            this.className = className;
            this.fieldType = fieldType;
            this.fieldName = fieldName;
            this.obfuscatedFieldName = obfuscatedFieldName;
        }
    }

    private Map<String, String> classNameMapping = new HashMap<String, String>();
    // Map of class name -> Map of obfuscated field name -> FieldInfo
    private Map<String, Map<String, FieldInfo>> fieldMapping = new HashMap<String, Map<String, FieldInfo>>();
    private Map<Integer, String> hprofStrings;


    public HprofUnobfuscator(String mappingFile, String hprofFile, String outFile) {
        MappingReader mappingReader = new MappingReader(new File(mappingFile));
        try {
            mappingReader.pump(this);
            UnobfuscatingProcessor unobfuscatingProcessor = new UnobfuscatingProcessor(new FileOutputStream(outFile));
            HprofReader hprofReader = new HprofReader(new FileInputStream(hprofFile), unobfuscatingProcessor);
            while (hprofReader.hasNext()) {
                hprofReader.next();
            }
            hprofStrings = unobfuscatingProcessor.getStrings();
            // Unobfuscate all class names first since they are needed when processing fields and methods
            for (ClassDefinition cls : unobfuscatingProcessor.getClasses().values()) {
                String name = hprofStrings.get(cls.getNameStringId());
                if (classNameMapping.containsKey(name)) {
//                    System.out.println("Unobfuscating " + name + " to " + classNameMapping.get(name));
                    hprofStrings.put(cls.getNameStringId(), classNameMapping.get(name));
                }
            }
            // Unobfuscate field names
            Set<Integer> updatedStrings = new HashSet<Integer>();
            for (ClassDefinition cls : unobfuscatingProcessor.getClasses().values()) {
                // Check if the class has any mapped fields
                String className = hprofStrings.get(cls.getNameStringId());
                if (!fieldMapping.containsKey(className)) {
                    continue;
                }
                Map<String, FieldInfo> mappedFields = fieldMapping.get(className);
                for (StaticField field : cls.getStaticFields()) {
                    String obfuscatedFieldName = hprofStrings.get(field.getFieldNameId());
                    if (mappedFields.containsKey(obfuscatedFieldName)) {
                        if (updatedStrings.contains(field.getFieldNameId())) {
                            throw new IllegalStateException("Multiple references to obfuscated string " + field.getFieldNameId());
                        }
                        updatedStrings.add(field.getFieldNameId());
                        System.out.println("Unobfuscating static field " + obfuscatedFieldName + " to " + mappedFields.get(obfuscatedFieldName).fieldName + " in " + className);
                    }
                }
            }
        }
        catch (IOException e) {
            System.err.println("Failed to convert hprof file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new HprofUnobfuscator("/Users/erikandre/temp/hprof/mapping.txt", "/Users/erikandre/temp/hprof/obfuscated.hprof", "/Users/erikandre/temp/hprof/out.hprof");
    }

    @Override
    public boolean processClassMapping(String className, String newClassName) {
        classNameMapping.put(newClassName, className);
        return true;
    }

    @Override
    public void processFieldMapping(String className, String fieldType, String fieldName, String newFieldName) {
//        System.out.println("Field mapping, class:" + className + ", fieldType:" + fieldType + ", fieldName:" + fieldName + " ->" + newFieldName);
        FieldInfo field = new FieldInfo(className, fieldType, fieldName, newFieldName);
        if (!fieldMapping.containsKey(className)) {
            Map<String, FieldInfo> map = new HashMap<String, FieldInfo>();
            fieldMapping.put(className, map);
        }
        Map<String, FieldInfo> map = fieldMapping.get(className);
        map.put(newFieldName, field);
    }

    @Override
    public void processMethodMapping(String className, int firstLineNumber, int lastLineNumber, String methodReturnType, String methodName, String methodArguments, String newMethodName) {
//        System.out.println("Method mapping " + className + "." + methodName + "->" + newMethodName);
    }

}
