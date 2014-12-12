package com.badoo.bmd.model;

import java.util.List;

/**
 * Created by Erik Andre on 23/10/14.
 */
public class BmdInstanceDump {

    private final int id;
    private final int classId;
    private final List<BmdInstanceDumpField> fields;

    public BmdInstanceDump(int id, int classId, List<BmdInstanceDumpField> fields) {
        this.id = id;
        this.classId = classId;
        this.fields = fields;
    }

    public int getId() {
        return id;
    }

    public int getClassId() {
        return classId;
    }

    public List<BmdInstanceDumpField> getFields() {
        return fields;
    }
}
