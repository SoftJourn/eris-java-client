package com.softjourn.eris.contract.types;

import com.softjourn.eris.contract.Util;

import java.math.BigInteger;

public class SolidityString extends Type<String> {

    private final int length;

    public SolidityString() {
        this.length = 0;
    }

    public SolidityString(int length) {
        this.length = length;
    }

    @Override
    public String formatInput(String value) {
        String result = Util.fromUtf8(value).substring(2);
        int length = result.length() / 2;
        double floor = Math.floor((result.length() + 63) / 64);
        result = Util.rightPad(result, (int) (floor * 64), '0');
        return new Uint().formatInput(BigInteger.valueOf(length)) + result;
    }

    @Override
    public String formatOutput(String value) {
        int length = Integer.parseInt(this.dynamicPart(value).substring(0, 64), 16) * 2;
        return Util.toUtf8(this.dynamicPart(value).substring(64, 64 + length));
    }

    @Override
    public boolean canRepresent(String value) {
        return false;
    }

    @Override
    public String toString() {
        return "string";
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public Class<String> valueClass() {
        return String.class;
    }

    @Override
    public boolean isType(String name) {
        return name.matches("^string(\\[([0-9]*)\\])*$");
    }

    @Override
    public Type<String> createFromName(String name) {
        return new SolidityString((getLength(name)));
    }

}
