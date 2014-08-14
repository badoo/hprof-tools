package com.badoo.hprof.unobfuscator;

import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.HprofWriter;
import com.badoo.hprof.library.Tag;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.library.model.NamedField;
import com.badoo.hprof.library.model.StaticField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import proguard.obfuscate.MappingProcessor;
import proguard.obfuscate.MappingReader;

import static com.badoo.hprof.library.IoUtil.writeInt;

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
    // Map of string id -> string
    private Map<Integer, String> hprofStrings;


    public HprofUnobfuscator(String mappingFile, String hprofFile, String outFile) {
        MappingReader mappingReader = new MappingReader(new File(mappingFile));
        try {
            mappingReader.pump(this);
            DataCollectionProcessor dataCollectionProcessor = new DataCollectionProcessor();
            HprofReader hprofReader = new HprofReader(new FileInputStream(hprofFile), dataCollectionProcessor);
            while (hprofReader.hasNext()) {
                hprofReader.next();
            }
            hprofStrings = dataCollectionProcessor.getStrings();
            // Unobfuscate all class names first since they are needed when processing fields and methods
            for (ClassDefinition cls : dataCollectionProcessor.getClasses().values()) {
                unobfuscateClass(cls);
            }
            // Unobfuscate field names
            for (ClassDefinition cls : dataCollectionProcessor.getClasses().values()) {
                // Check if the class has any mapped fields
                String className = hprofStrings.get(cls.getNameStringId());
                if (!fieldMapping.containsKey(className)) {
                    continue;
                }
                Map<String, FieldInfo> mappedFields = fieldMapping.get(className);
                for (StaticField field : cls.getStaticFields()) {
                    unobfuscateField(className, field, mappedFields);
                }
                for (InstanceField field : cls.getInstanceFields()) {
                    unobfuscateField(className, field, mappedFields);
                }
            }
            // Start the second pass where we write the modified hprof file to the output stream
            OutputStream out = new FileOutputStream(outFile);
            StringUpdateProcessor updateProcessor = new StringUpdateProcessor(out, dataCollectionProcessor.getClasses(), dataCollectionProcessor.getStrings());
            hprofReader = new HprofReader(new FileInputStream(hprofFile), updateProcessor);
            while (hprofReader.hasNext()) {
                hprofReader.next();
            }
        }
        catch (IOException e) {
            System.err.println("Failed to convert hprof file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void unobfuscateClass(ClassDefinition cls) {
        String name = hprofStrings.get(cls.getNameStringId());
        if (classNameMapping.containsKey(name)) {
            System.out.println("Unobfuscating class " + name + " to " + classNameMapping.get(name));
            hprofStrings.put(cls.getNameStringId(), classNameMapping.get(name));
        }
    }

    private void unobfuscateField(String className, NamedField field, Map<String, FieldInfo> mappedFields) {
        String obfuscatedFieldName = hprofStrings.get(field.getFieldNameId());
        if (mappedFields.containsKey(obfuscatedFieldName)) {
            System.out.println("Unobfuscating field " + obfuscatedFieldName + " to " + mappedFields.get(obfuscatedFieldName).fieldName + " in " + className);
            hprofStrings.put(field.getFieldNameId(), mappedFields.get(obfuscatedFieldName).fieldName);
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
        if (fieldName.equals(newFieldName)) {
            return;
        }
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
        // Not used since hprof files do not contain method information
    }

}
