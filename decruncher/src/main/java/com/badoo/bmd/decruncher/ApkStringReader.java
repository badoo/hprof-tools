package com.badoo.bmd.decruncher;

import com.googlecode.dex2jar.Field;
import com.googlecode.dex2jar.Method;
import com.googlecode.dex2jar.reader.DexFileReader;
import com.googlecode.dex2jar.visitors.DexAnnotationVisitor;
import com.googlecode.dex2jar.visitors.DexClassVisitor;
import com.googlecode.dex2jar.visitors.DexFieldVisitor;
import com.googlecode.dex2jar.visitors.DexFileVisitor;
import com.googlecode.dex2jar.visitors.DexMethodVisitor;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Utility class for reading strings (class and field names) from an APK file.
 *
 * Created by Erik Andre on 17/12/14.
 */
public class ApkStringReader {

    @Nonnull
    public static Set<String> readStrings(@Nonnull File in) throws IOException {
        final Set<String> strings = new HashSet<String>();
        final DexClassVisitor classVisitor = new DexClassVisitor() {
            @Override
            public void visitSource(String s) {
                // Ignored
            }

            @Override
            public DexFieldVisitor visitField(int i, Field field, Object o) {
                strings.add(field.getName());
                return null;
            }

            @Override
            public DexMethodVisitor visitMethod(int i, Method method) {
                return null; // Ignored
            }

            @Override
            public void visitEnd() {

            }

            @Override
            public DexAnnotationVisitor visitAnnotation(String s, boolean b) {
                return null;
            }
        };
        final DexFileVisitor fileVisitor = new DexFileVisitor() {
            @Override
            public DexClassVisitor visit(int access_flags, String className, String superClass, String[] interfaceNames) {
                className = className.substring(1, className.length() - 1);
                className = className.replace("/", ".");
                strings.add(className);
                return classVisitor;
            }

            @Override
            public void visitEnd() {
            }
        };
        DexFileReader reader = new DexFileReader(in);
        reader.accept(fileVisitor);
        return strings;
    }
}
