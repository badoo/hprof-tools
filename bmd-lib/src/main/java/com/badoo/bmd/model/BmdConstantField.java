package com.badoo.bmd.model;

/**
 * Data class for constant fields in BMD class definitions.
 *
 * Created by Erik Andre on 23/10/14.
 */
@SuppressWarnings("UnusedDeclaration")
public class BmdConstantField {

    private final int index;
    private final BmdBasicType type;
    private final Integer intValue;
    private final Boolean boolValue;
    private final Byte byteValue;
    private final Character charValue;
    private final Float floatValue;
    private final Double doubleValue;
    private final Long longValue;
    private final Short shortValue;

    public BmdConstantField(int index, BmdBasicType type, Integer intValue) {
        this(index, type, intValue, null, null, null, null, null, null, null);
    }

    public BmdConstantField(int index, BmdBasicType type, Boolean boolValue) {
        this(index, type, null, boolValue, null, null, null, null, null, null);
    }

    public BmdConstantField(int index, BmdBasicType type, Byte byteValue) {
        this(index, type, null, null, byteValue, null, null, null, null, null);
    }

    public BmdConstantField(int index, BmdBasicType type, Character charValue) {
        this(index, type, null, null, null, charValue, null, null, null, null);
    }

    public BmdConstantField(int index, BmdBasicType type, Float floatValue) {
        this(index, type, null, null, null, null, floatValue, null, null, null);
    }

    public BmdConstantField(int index, BmdBasicType type, Double doubleValue) {
        this(index, type, null, null, null, null, null, doubleValue, null, null);
    }

    public BmdConstantField(int index, BmdBasicType type, Long longValue) {
        this(index, type, null, null, null, null, null, null, longValue, null);
    }

    public BmdConstantField(int index, BmdBasicType type, Short shortValue) {
        this(index, type, null, null, null, null, null, null, null, shortValue);
    }

    private BmdConstantField(int index, BmdBasicType type, Integer intValue, Boolean boolValue, Byte byteValue, Character charValue, Float floatValue, Double doubleValue, Long longValue, Short shortValue) {
        this.index = index;
        this.type = type;
        this.intValue = intValue;
        this.boolValue = boolValue;
        this.byteValue = byteValue;
        this.charValue = charValue;
        this.floatValue = floatValue;
        this.doubleValue = doubleValue;
        this.longValue = longValue;
        this.shortValue = shortValue;
    }

    public BmdBasicType getType() {
        return type;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public Boolean getBoolValue() {
        return boolValue;
    }

    public Byte getByteValue() {
        return byteValue;
    }

    public Character getCharValue() {
        return charValue;
    }

    public Float getFloatValue() {
        return floatValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public Short getShortValue() {
        return shortValue;
    }

    public int getIndex() {
        return index;
    }
}
