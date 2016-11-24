package com.softjourn.eris.contract.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.softjourn.eris.contract.Util.encodeInt;

public class Array<P> extends Type<List<P>> {

    private static final int DYNAMIC_ARRAY_LENGTH = -1;

    private Type<P> type;

    private int staticArrayLength;

    public Array(Type<P> type) {
        this(type, DYNAMIC_ARRAY_LENGTH);
    }

    public Array(Type<P> type, int staticArrayLength) {
        this.type = type;
        this.staticArrayLength = staticArrayLength;
    }

    @Override
    public String formatInput(List<P> value) {
        return value.stream()
                .map(v -> type.formatInput(v))
                .collect(Collectors.joining("", isDynamic() ? encodeInt(value.size()) : "", ""));
    }


    @Override
    public List<P> formatOutput(String value) {
        int length = readLength(value);
        checkLength(value, length);
        List<P> result = new ArrayList<>();
        for (int i = 1; i <= length; i++) {
            result.add(type.formatOutput(getIthPart(value, i)));
        }
        return result;
    }

    private int readLength(String value) {
        return Integer.parseInt(value.substring(0, 64), 16);
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
                if (!type.canRepresent(getIthPart(value, i))) return false;
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return type.toString() + "[]";
    }

    @Override
    public boolean isDynamic() {
        return staticArrayLength == DYNAMIC_ARRAY_LENGTH;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<List<P>> valueClass() {
        return (Class<List<P>>) Collections.emptyList().getClass().getSuperclass();
    }

    @Override
    public boolean isType(String name) {
        return name.matches(".*(\\[(\\d)*\\])+$") && type.isType(name.substring(0, name.indexOf('[')));
    }

    @Override
    public Type<List<P>> createFromName(String name) {
        return new Array<>(type.createFromName(getSubName(name)), geLength(name));
    }

    private int geLength(String name) {
        String s = name.substring(name.lastIndexOf('[')).replaceAll("[\\[\\]\\s]", "");
        return s.isEmpty() ? DYNAMIC_ARRAY_LENGTH : Integer.parseInt(s);
    }

    static String getSubName(String typeName) {
        return typeName.substring(0, typeName.lastIndexOf('['));
    }

    @Override
    int getStaticArrayLength() {
        return staticArrayLength == DYNAMIC_ARRAY_LENGTH ? 1 : staticArrayLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Array<?> array = (Array<?>) o;

        return type.equals(array.type);

    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
