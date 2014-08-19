package com.badoo.hprof.deobfuscator;

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

    public String getClassName(String obfuscatedName) {
        return classNameMapping.get(obfuscatedName);
    }

    public Map<String, FieldInfo> getFieldMappingsForClass(String className) {
        return fieldMapping.get(className);
    }

    @Override
    public boolean processClassMapping(String className, String newClassName) {
        classNameMapping.put(newClassName, className);
        return true;
    }

    @Override
    public void processFieldMapping(String className, String fieldType, String fieldName, String newFieldName) {
        if (fieldName.equals(newFieldName)) {
            return; // Ignore mappings that are not obfuscated
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
