package com.softjourn.eris.contract.types;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.softjourn.eris.contract.Util.leftPad;

public class Array<P> extends Type<List<P>> {

    private Type<P> type;

    public Array(Type<P> type) {
        this.type = type;
    }

    @Override
    public String formatInput(List<P> value) {
        return value.stream()
                .map(v -> type.formatInput(v))
                .collect(Collectors.joining("", getLengthRepresentation(value), ""));
    }

    private String getLengthRepresentation(List<P> value) {
        return leftPad(BigInteger.valueOf(value.size()).toString(16), 64, '0');
    }

    @Override
    public List<P> formatOutput(String value) {
        int length = readLength(value);
        checkLength(value, length);
        List<P> result = new ArrayList<>();
        for (int i = 1; i <= length; i++){
            result.add(type.formatOutput(getIthPart(value, i)));
        }
        return result;
    }

    private int readLength(String value) {
        return Integer.parseInt(value.substring(0,64), 16);
    }

    private void checkLength(String value, int length) {
        if (value.length() != (length + 1) * 64) {
            throw new IllegalArgumentException("Wrong input. Length of array should be " + length +
                    " but presented length of input value is " + value.length());
        }
    }

    private String getIthPart(String value, int i) {
        return value.substring(i * 64, (i + 1) * 64);
    }

    @Override
    public boolean canRepresent(String value) {
        try {
            int length = readLength(value);
            checkLength(value, length);
            for (int i = 1; i <= length; i++) {
                if (! type.canRepresent(getIthPart(value, i))) return false;
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<List<P>> valueClass() {
        return (Class<List<P>>) Collections.emptyList().getClass();
    }

}
