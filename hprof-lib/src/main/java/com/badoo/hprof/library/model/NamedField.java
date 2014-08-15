package com.badoo.hprof.library.model;

/**
 * Base class for named fields (instance and static fields)
 * <p/>
 * Created by Erik Andre on 14/08/2014.
 */
public interface NamedField {

    /**
     * Returns the field name string string id.
     *
     * @return The field name string id
     */
    int getFieldNameId();

    /**
     * Set the field name string id.
     *
     * @param fieldNameId The field name string id
     */
    void setFieldNameId(int fieldNameId);
}
