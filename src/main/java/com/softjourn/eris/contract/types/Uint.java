package com.softjourn.eris.contract.types;


import com.softjourn.eris.contract.Util;

import java.math.BigInteger;

public class Uint extends Type<BigInteger> {

    private static final int DEFAULT_INPUT_LENGTH = 64;

    private static final char DEFAULT_PADDING_CHAR = '0';

    private final int length;

    public Uint() {
        length = 0;
    }

    public Uint(int length) {
        this.length = length;
    }

    public boolean isType(String name) {
        return name.matches("^uint[0-9]*$");
    }

    @Override
    public Type<BigInteger> createFromName(String name) {
        return new Uint(getLength(name));
    }

    @Override
    public String toString() {
        return "uint" + (length > 0 ? length : "");
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public Class<BigInteger> valueClass() {
        return BigInteger.class;
    }

    @Override
    public String formatInput(BigInteger value) {
        return Util.leftPad(value.toString(16), DEFAULT_INPUT_LENGTH, DEFAULT_PADDING_CHAR).toUpperCase();
    }

    @Override
    public BigInteger formatOutput(String value) {
        return new BigInteger(value, 16);
    }

    @Override
    public boolean canRepresent(String value) {
        return canParse(value);
    }

    private boolean canParse(String val) {
        try {
            new BigInteger(val, 16);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Uint uint = (Uint) o;

        return length == uint.length;

    }

    @Override
    public int hashCode() {
        return length;
    }
}
