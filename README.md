Introduction
==================

Hprof-deobfuscator is a tool for deobfuscating HPROF memory dump files that are created from builds obfuscated with either ProGuard or DexGuard.

Building
========

The application can be built by executing the following Gradle command in the root of the git:

<code>
./gradlew jarWithDependencies
</code>

This will create a jar file containing the application and its external dependencies in the following location:

<code>
./deobfuscator/build/libs/deobfuscator-all-1.0.jar
</code>

Usage
=====

After building the application you can execute it with the following command:

<code>
./deobfuscator/build/libs/deobfuscator-all-1.0.jar \<mapping file\> \<input hprof\> \<output hprof\>
</code>




