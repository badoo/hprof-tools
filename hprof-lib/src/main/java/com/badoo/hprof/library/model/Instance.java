package com.badoo.hprof.library.model;

/**
 * Created by Erik Andre on 17/07/2014.
 */
public class Instance {

    private final int objectId;
    private final int stackTraceSerialId;
    private final int classObjectId;
    private final byte[] instanceFieldData;

    public Instance(int objectId, int stackTraceSerialId, int classObjectId, byte[] instanceFieldData) {
        this.objectId = objectId;
        this.stackTraceSerialId = stackTraceSerialId;
        this.classObjectId = classObjectId;
        this.instanceFieldData = instanceFieldData;
    }
}
