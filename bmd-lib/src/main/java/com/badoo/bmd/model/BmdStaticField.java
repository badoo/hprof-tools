package com.badoo.bmd.model;

/**
 * Data class for static fields in BMD class definitions.
 * <p/>
 * Created by Erik Andre on 02/11/14.
 */
@SuppressWarnings("UnusedDeclaration")
public class BmdStaticField {

    private final int nameId;
    private final BmdBasicType type;
    private Integer intValue;
    private Boolean boolValue;
    private Byte byteValue;
    private Character charValue;
    private Float floatValue;
    private Double doubleValue;
    private Long longValue;
    private Short shortValue;

    public BmdStaticField(int nameId, BmdBasicType type) {
        this.nameId = nameId;
        this.type = type;
    }

    public int getNameId() {
        return nameId;
    }

    public BmdBasicType getType() {
        return type;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public Boolean getBoolValue() {
        return boolValue;
    }

    public void setBoolValue(Boolean boolValue) {
        this.boolValue = boolValue;
    }

    public Byte getByteValue() {
        return byteValue;
    }

    public void setByteValue(Byte byteValue) {
        this.byteValue = byteValue;
    }

    public Character getCharValue() {
        return charValue;
    }

    public void setCharValue(Character charValue) {
        this.charValue = charValue;
    }

    public Float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Short getShortValue() {
        return shortValue;
    }

    public void setShortValue(Short shortValue) {
        this.shortValue = shortValue;
    }
}
