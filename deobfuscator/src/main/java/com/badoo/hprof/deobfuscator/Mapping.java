package com.badoo.hprof.deobfuscator;

import java.nio.channels.MembershipKey;
import java.util.HashMap;
import java.util.Map;

import proguard.obfuscate.MappingProcessor;

/**
* Created by Erik Andre on 19/08/2014.
*/
class Mapping implements MappingProcessor {

    public static class FieldInfo {

        final String className;
        final String fieldType;
        final String fieldName;
        final String obfuscatedFieldName;
        final String newClassName;

        private FieldInfo(String className, String fieldType, String fieldName,String newClassName,  String obfuscatedFieldName) {
            this.className = className;
            this.fieldType = fieldType;
            this.fieldName = fieldName;
            this.obfuscatedFieldName = obfuscatedFieldName;
            this.newClassName = newClassName;
        }

        @Override
        public String toString() {
            return "FieldInfo{" +
                    "className='" + className + '\'' +
                    ", fieldType='" + fieldType + '\'' +
                    ", fieldName='" + fieldName + '\'' +
                    ", obfuscatedFieldName='" + obfuscatedFieldName + '\'' +
                    '}';
        }
    }

    public static class MethodInfoKey
    {
        final String signature;
        final String methodName;

        public MethodInfoKey(String signature, String methodName) {
            this.signature = signature;
            this.methodName = methodName;
        }

        @Override
        public boolean equals(Object obj) {

            if( !( obj instanceof  MethodInfoKey))
                return false;

            MethodInfoKey infoKey = (MethodInfoKey) obj;
            return this.methodName.equals(infoKey.methodName) && this.signature.equals(infoKey.signature);


        }

        @Override
        public int hashCode() {
            return (signature+":"+ methodName).hashCode();
        }

        @Override
        public String toString() {
            return "MethodInfoKey{" +
                    "signature='" + signature + '\'' +
                    ", methodName='" + methodName + '\'' +
                    '}';
        }
    }


    public static class MethodInfo {

        final String className;
        final int firstLineNumber;
        final int lastLineNumber;
        final String methodReturnType;
        final String methodName;
        final String methodArguments;
        final String obfuscatedMethodName;
        final int newFirstLineNumber;
        final int newLastLineNumber;
        final String newClassName;

        private MethodInfo(String className , int firstLineNumber, int lastLineNumber, String methodReturnType, String methodName, String methodArguments, String newClassName, int newFirstLineNumber, int newLastLineNumber, String newMethodName) {
            this.className = className;
            this.methodName = methodName;
            this.firstLineNumber = firstLineNumber;
            this.lastLineNumber = lastLineNumber;
            this.methodReturnType = methodReturnType;
            this.methodArguments = methodArguments;
            this.obfuscatedMethodName = newMethodName;
            this.newFirstLineNumber = newFirstLineNumber;
            this.newLastLineNumber = newLastLineNumber;
            this.newClassName = newClassName;

        }


        @Override
        public String toString() {
            return "MethodInfo{" +
                    "className='" + className + '\'' +
                    ", firstLineNumber=" + firstLineNumber +
                    ", lastLineNumber=" + lastLineNumber +
                    ", methodReturnType='" + methodReturnType + '\'' +
                    ", methodName='" + methodName + '\'' +
                    ", methodArguments='" + methodArguments + '\'' +
                    ", obfuscatedMethodName='" + obfuscatedMethodName + '\'' +
                    '}';
        }
    }



    private Map<String, String> classNameMapping = new HashMap<String, String>();
    // Map of class name -> Map of obfuscated field name -> FieldInfo
    private Map<String, Map<String, FieldInfo>> fieldMapping = new HashMap<String, Map<String, FieldInfo>>();
    // Map of string id -> string

    private Map<String, Map<MethodInfoKey,MethodInfo>> methodMapping = new HashMap<String, Map<MethodInfoKey, MethodInfo>>();

    public String getClassName(String obfuscatedName) {
        if (obfuscatedName.endsWith("[]")) {
          return getArrayClassName(obfuscatedName);
        }
        return classNameMapping.get(obfuscatedName);
    }

    private String getArrayClassName(final String obfuscatedName) {
        String componentName = getArrayComponentName(obfuscatedName);
        String suffix = obfuscatedName.substring(componentName.length());
        String deobfuscatedComponentName = classNameMapping.get(componentName);
        if (deobfuscatedComponentName == null) {
          return deobfuscatedComponentName;
        }

        return deobfuscatedComponentName.concat(suffix);
    }

    private String getArrayComponentName(final String className) {
        int endIndex = className.length();
        while (endIndex >= 0
            && (className.charAt(endIndex - 2) == '[')
            && (className.charAt(endIndex - 1) == ']')) {
          endIndex -= 2;
        }
        return className.substring(0, endIndex);
    }

    public Map<String, FieldInfo> getFieldMappingsForClass(String className) {
        return fieldMapping.get(className);
    }


    public Map<MethodInfoKey, MethodInfo> getMethodMappingsForClass(String className) {
        return methodMapping.get(className);
    }

    @Override
    public boolean processClassMapping(String className, String newClassName) {

        // Modify the class qualified name from com.example.Test to com/example/Test to match the HPROF format
        newClassName =  newClassName.replace('.','/');
        className= className.replace('.','/');
        classNameMapping.put(newClassName, className);
        return true;
    }

    @Override
    public void processFieldMapping(String className, String fieldType, String fieldName, String newClassName,String newFieldName) {


        className=className.replace('.','/');

        if (fieldName.equals(newFieldName)) {
            return; // Ignore mappings that are not obfuscated
        }

        FieldInfo field = new FieldInfo(className, fieldType, fieldName,newClassName, newFieldName);
        if (!fieldMapping.containsKey(className)) {
            Map<String, FieldInfo> map = new HashMap<String, FieldInfo>();
            fieldMapping.put(className, map);
        }
        Map<String, FieldInfo> map = fieldMapping.get(className);
        map.put(newFieldName, field);
    }






    @Override
    public void processMethodMapping(String className, int firstLineNumber, int lastLineNumber, String methodReturnType, String methodName, String methodArguments, String newClassName, int newFirstLine, int newLastLine,String newMethodName ) {

        className=className.replace('.','/');
        methodArguments = methodArguments.replace('.','/');
        methodReturnType = methodReturnType.replace('.','/');


        if (methodName.equals(newMethodName)) {
            return; // Ignore mappings that are not obfuscated
        }

        MethodInfo method = new MethodInfo(className,firstLineNumber,lastLineNumber,methodReturnType,methodName,methodArguments,newClassName, newFirstLine, newLastLine, newMethodName);
        if (!methodMapping.containsKey(className)) {
            Map<MethodInfoKey, MethodInfo> map = new HashMap<MethodInfoKey, MethodInfo>();
            methodMapping.put(className, map);
        }
        Map<MethodInfoKey, MethodInfo> map = methodMapping.get(className);
        map.put(new MethodInfoKey(methodArguments,newMethodName), method);


    }

    @Override
    public String toString() {
        return "Mapping{" +
                "classNameMapping=" + classNameMapping +
                ", fieldMapping=" + fieldMapping +
                ", methodMapping=" + methodMapping +
                '}';
    }
}
