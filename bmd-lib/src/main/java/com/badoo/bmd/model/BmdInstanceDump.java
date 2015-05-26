package com.badoo.bmd.model;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Class containing the data of an BMD instance dump including the instance id, class id and the values of instance fields.
 * <p/>
 * Created by Erik Andre on 23/10/14.
 */
public class BmdInstanceDump {

    private final int id;
    private final int classId;
    private final List<BmdInstanceDumpField> fields;

    public BmdInstanceDump(int id, int classId, @Nonnull List<BmdInstanceDumpField> fields) {
        this.id = id;
        this.classId = classId;
        this.fields = fields;
    }

    /**
     * Returns the id of the object.
     *
     * @return The object id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the id of the class which the dump is an instance of.
     *
     * @return The class id of the dumped instance's class.
     */
    public int getClassId() {
        return classId;
    }

    /**
     * Returns the list of the fields of the dumped instance.
     *
     * @return A list of instance fields
     */
    @Nonnull
    public List<BmdInstanceDumpField> getFields() {
        return fields;
    }
}
