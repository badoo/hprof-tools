package com.badoo.hprof.deobfuscator;

import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.library.model.NamedField;
import com.badoo.hprof.library.model.StaticField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import proguard.obfuscate.MappingReader;

import static com.badoo.hprof.deobfuscator.Mapping.FieldInfo;

/**
 * Deobfuscator for hprof files obfuscated using proguard/dexguard.
 * <p/>
 * Deobfuscation is performed in two passes over the input file:
 * <p/>
 * 1. Read all strings, class and field names. Deduplicate all shared strings for field names.
 * 2. Write a modified copy of the input file with strings and class definitions modified.
 * <p/>
 * Input is the mapping file and the obfuscated hprof file.
 * <p/>
 * Created by Erik Andre on 13/08/2014.
 */
public class HprofDeobfuscator {

    private Map<Integer, HprofString> hprofStrings;
    private boolean debug;

    public HprofDeobfuscator(String mappingFile, String hprofFile, String outFile) {
        MappingReader mappingReader = new MappingReader(new File(mappingFile));
        Mapping mapping = new Mapping(); // The mapping between obfuscated and original names
        try {
            mappingReader.pump(mapping);
            DataCollectionProcessor dataCollectionProcessor = new DataCollectionProcessor();
            HprofReader hprofReader = new HprofReader(new FileInputStream(hprofFile), dataCollectionProcessor);
            while (hprofReader.hasNext()) {
                hprofReader.next(); // Read all strings and class definitions from the hprof file but don't write the output yet
            }
            hprofStrings = dataCollectionProcessor.getStrings();
            // Unobfuscate all class names first since they are needed when processing fields and methods
            for (ClassDefinition cls : dataCollectionProcessor.getClasses().values()) {
                deobfuscateClassName(cls, mapping);
            }
            // Deobfuscate field names
            for (ClassDefinition cls : dataCollectionProcessor.getClasses().values()) {
                // Check if the class has any mapped fields
                HprofString className = hprofStrings.get(cls.getNameStringId());
                Map<String, FieldInfo> mappedFields = mapping.getFieldMappingsForClass(className.getValue()); // Map of
                if (mappedFields == null || mappedFields.isEmpty()) {
                    continue;
                }
                // Deobfuscate the fields one by one
                for (StaticField field : cls.getStaticFields()) {
                    deobfuscateFieldName(className.getValue(), field, mappedFields);
                }
                for (InstanceField field : cls.getInstanceFields()) {
                    deobfuscateFieldName(className.getValue(), field, mappedFields);
                }
            }
            // Start the second pass where we write the modified hprof file to the output stream
            OutputStream out = new FileOutputStream(outFile);
            StringUpdateProcessor updateProcessor = new StringUpdateProcessor(out, dataCollectionProcessor.getClasses(), hprofStrings.values());
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

    public static void main(String args[]) {
        for (String s : args) {
            System.out.println("Arg: " + s);
        }
        //TODO Remove hard coded strings
        new HprofDeobfuscator("/Users/erikandre/temp/hprof/mapping.txt", "/Users/erikandre/temp/hprof/obfuscated.hprof", "/Users/erikandre/temp/hprof/out.hprof");
    }

    private void deobfuscateClassName(ClassDefinition cls, Mapping mapping) {
        HprofString name = hprofStrings.get(cls.getNameStringId());
        String deobfuscatedName = mapping.getClassName(name.getValue());
        if (deobfuscatedName != null) {
            if (debug) {
                System.out.println("Deobfuscating class " + name + " to " + deobfuscatedName);
            }
            name.setValue(deobfuscatedName);
        }
    }

    private void deobfuscateFieldName(String className, NamedField field, Map<String, FieldInfo> mappedFields) {
        HprofString fieldName = hprofStrings.get(field.getFieldNameId());
        if (mappedFields.containsKey(fieldName.getValue())) {
            if (debug) {
                System.out.println("Deobfuscating field " + fieldName + " to " + mappedFields.get(fieldName.getValue()).fieldName + " in " + className);
            }
            fieldName.setValue(mappedFields.get(fieldName.getValue()).fieldName);
        }
    }

}
