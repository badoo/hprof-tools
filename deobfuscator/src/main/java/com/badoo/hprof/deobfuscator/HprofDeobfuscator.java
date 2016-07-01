package com.badoo.hprof.deobfuscator;

import com.badoo.hprof.library.HprofReader;
import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.HprofString;
import com.badoo.hprof.library.model.ID;
import com.badoo.hprof.library.model.InstanceField;
import com.badoo.hprof.library.model.NamedField;
import com.badoo.hprof.library.model.StackFrame;
import com.badoo.hprof.library.model.StaticField;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private Map<ID, HprofString> hprofStrings;
    private Set<ID> hprofHandledSignatureStrings = new HashSet<ID>();
    private boolean debug;

    public HprofDeobfuscator(String mappingFile, String hprofFile, String outFile, boolean debug) {
        this.debug = debug;
        MappingReader mappingReader = new MappingReader(new File(mappingFile));
        Mapping mapping = new Mapping(); // The mapping between obfuscated and original names
        try {
            mappingReader.pump(mapping);

            if (debug)
                System.out.println(mapping);


            DataCollectionProcessor dataCollectionProcessor = new DataCollectionProcessor();
            HprofReader hprofReader = new HprofReader(new FileInputStream(hprofFile), dataCollectionProcessor);
            while (hprofReader.hasNext()) {
                hprofReader.next(); // Read all strings and class definitions from the hprof file but don't write the output yet
            }
            hprofStrings = dataCollectionProcessor.getStrings();

            System.out.println(hprofStrings);

            // Deobfuscate all class names first since they are needed when processing fields and methods
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


            for(StackFrame stackFrame:dataCollectionProcessor.getStackFrames().values())
            {
//                System.out.println(dataCollectionProcessor.getClassesWithSerialNumbers());
                ClassDefinition cls = dataCollectionProcessor.getClassesWithSerialNumbers().get(stackFrame.getClassSerialNumber());

//                System.out.println("cls:" +cls);

                HprofString className = hprofStrings.get(cls.getNameStringId());
                Map<Mapping.MethodInfoKey, Mapping.MethodInfo> mappedMethods = mapping.getMethodMappingsForClass(className.getValue()); // Map of

                if (mappedMethods == null || mappedMethods.isEmpty()) {
                    System.out.println("METHOD:"+ hprofStrings.get(stackFrame.getMethodNameStringId()));
                    System.out.println("METHOD_SIGN:"+ hprofStrings.get(stackFrame.getMethodSignatureStringId()));
                    System.out.println("FILE_NAME:"+ hprofStrings.get(stackFrame.getSourceFileNameStringId()));

                    continue;
                }
                deobfuscateMethodName(className.getValue(), stackFrame,mappedMethods,mapping);



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
        if (args.length < 3) {

        }
        String mappingFile;
        String hprofIn;
        String hprofOut;
        boolean debug = false;
        if (args.length == 4 && args[0].equals("-v")) {
            debug = true;
            mappingFile = args[1];
            hprofIn = args[2];
            hprofOut = args[3];
        } else if (args.length == 3) {
            mappingFile = args[0];
            hprofIn = args[1];
            hprofOut = args[2];
        } else {
            System.err.println("Usage:");
            System.err.println("java -jar deobfuscator.jar [-v] <mapping file> <obfuscated hprof file> <output hprof file>");
            return;
        }
        new HprofDeobfuscator(mappingFile, hprofIn, hprofOut, debug);
    }

    private void deobfuscateClassName(ClassDefinition cls, Mapping mapping) {

        HprofString name = hprofStrings.get(cls.getNameStringId());
//        System.out.println(name);
        String classNameString = name.getValue();
//        String dottedClassName = classNameString.replace('/','.');
        String deobfuscatedName = mapping.getClassName(classNameString);

        if (deobfuscatedName != null) {
            if (debug) {
                System.out.println("Deobfuscating class " + name + " to " + deobfuscatedName);
            }
//            String slasheddeobfuscatedName = dotteddeobfuscatedName.replace('.','/');
            name.setValue(deobfuscatedName);
        }
    }

    private void deobfuscateFieldName(String className, NamedField field, Map<String, FieldInfo> mappedFields) {
        HprofString fieldName = hprofStrings.get(field.getFieldNameId());

//        if(debug) {
////            System.out.println("FIELD: " + fieldName.getValue());
//        }

        if (mappedFields.containsKey(fieldName.getValue())) {
            if (debug) {
                System.out.println("Deobfuscating field " + fieldName.getValue() + " to " + mappedFields.get(fieldName.getValue()).fieldName + " in " + className);
            }
            fieldName.setValue(mappedFields.get(fieldName.getValue()).fieldName);
        }
    }

private static String CLASS_MATCH_PATTERN = "(L)([^;]*)(;)";
    private void deobfuscateMethodName(String className, StackFrame stackFrame, Map<Mapping.MethodInfoKey, Mapping.MethodInfo> mappedMethods, Mapping mapping) {
        HprofString methodName = hprofStrings.get(stackFrame.getMethodNameStringId());
        HprofString methodSignature = hprofStrings.get(stackFrame.getMethodSignatureStringId());

        if(!hprofHandledSignatureStrings.contains(methodSignature.getId())) {
            Pattern p = Pattern.compile(CLASS_MATCH_PATTERN);
            Matcher matcher = p.matcher(methodSignature.getValue());

            StringBuffer sb = new StringBuffer();
            while(matcher.find())
            {
                String qualifiedObfuscatedClassName = matcher.group(2);

                String qualifiedDeobfuscatedClassName  = mapping.getClassName(qualifiedObfuscatedClassName);
                if(qualifiedDeobfuscatedClassName ==null)
                {
                    qualifiedDeobfuscatedClassName = qualifiedObfuscatedClassName;
                }
                qualifiedDeobfuscatedClassName = qualifiedDeobfuscatedClassName.replace("$", "\\$");
                matcher.appendReplacement(sb,"$1"+qualifiedDeobfuscatedClassName+"$3");
            }
            matcher.appendTail(sb);
            methodSignature.setValue(sb.toString());
            hprofHandledSignatureStrings.add(methodSignature.getId());





        }

        String methodSignatureString = methodSignature.getValue().substring(1, methodSignature.getValue().indexOf(')'));
        String proguardMethodSignature = toProguardMapping(methodSignatureString);



        if(debug) {
            System.out.println("METHOD: " + methodName.getValue()+",PROGUARD_METHOD_SIGN: " + proguardMethodSignature);

        }

        Mapping.MethodInfoKey methodInfoKey = new Mapping.MethodInfoKey(proguardMethodSignature,methodName.getValue());

        System.out.println("MethodInfoKey: "+ methodInfoKey);

        if (mappedMethods.containsKey(methodInfoKey)) {
            if (debug) {
                System.out.println("Deobfuscating method " + methodName.getValue() + " to " + mappedMethods.get(methodInfoKey).methodName + " in " + className);
            }
            methodName.setValue(mappedMethods.get(methodInfoKey).methodName);
        }
    }



    private String toProguardMapping(String hprofMethodSignature)
    {
        StringBuffer sb= new StringBuffer();

        int arrayCount = 0;

        methodSignature:        for(int i=0;i<hprofMethodSignature.length(); i++ )
        {

            switch(hprofMethodSignature.charAt(i))
            {

                case 'I':
                    sb.append("int");
                    break;
                case 'J':
                    sb.append("long");
                    break;
                case 'Z':
                    sb.append("boolean");
                    break;
                case 'B':
                    sb.append("byte");
                    break;
                case 'C':
                    sb.append("char");
                    break;
                case 'S':
                    sb.append("short");
                    break;
                case 'F':
                    sb.append("float");
                    break;
                case 'D':
                    sb.append("double");
                    break;
                case '[':
                    arrayCount++;
                    continue methodSignature;
                case 'L':

                    while(hprofMethodSignature.charAt(++i) != ';') {

                        sb.append(hprofMethodSignature.charAt(i));
                    };



            }
            for(int j=0;j< arrayCount;j++)
             {
                sb.append("[]");

             }
            arrayCount = 0;

            if(i<hprofMethodSignature.length() -1)
                sb.append(',');
        }

        return sb.toString();

    }

}
