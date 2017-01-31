package com.softjourn.eris.contract.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonDeserialize(using = TypeDeserializer.class)
public abstract class Type<T> {

    /**
     * Format given value in accordance with this pojo
     * to be passed to ERIS
     *
     * @param value value to be formatted
     * @return formatted value
     */
    public abstract String formatInput(T value);

    /**
     * Format given value in accordance with this pojo
     * that is passed from ERIS
     *
     * @param value value to be formatted
     * @return formatted value
     */
    public abstract T formatOutput(String value);

    /**
     * Check if value can be instance of this pojo
     *
     * @param value value to be checked
     * @return true if this value can be represented by this pojo
     */
    public abstract boolean canRepresent(String value);

    /**
     * Returns name of pojo with length (i.e. uint256, bytes32)
     *
     * @return full name of this pojo
     */
    public abstract String toString();

    /**
     * @return true if this pojo is dynamic(has not predefined byte length like array)
     * and else otherwise
     */
    public abstract boolean isDynamic();

    /**
     * @return class of object it can represent
     */
    public abstract Class<T> valueClass();

    /**
     * @param name Name of pojo in compiled contract
     * @return true if this Type object is for this pojo name
     */
    public abstract boolean isType(String name);

    public abstract Type<T> createFromName(String name);

    public int staticPartLength() {
        return 32 * getStaticArrayLength();
    }

    /**
     * @return length of static part of array or 1
     * Should be overridden in arrays
     */
    int getStaticArrayLength() {
        return 1;
    }

    static Type suggestType(String typeName) {
        for (Type type : allDefinedTypes()) {
            if (type.isType(typeName)) {
                return type.createFromName(typeName);
            }
        }
        throw new RuntimeException("Type for " + typeName + " is not registered yet.");
    }

    static int getLength(String typeName) {
        Pattern pattern = Pattern.compile("([0-9]+)(\\[\\])?$");
        Matcher matcher = pattern.matcher(typeName);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    private static Type[] allDefinedTypes() {
        return new Type[]{
                new Uint(),
                new Address(),
                new Bool(),
                new Bytes(),
                new Array(new Uint()),
                new Array(new Address()),
                new Array(new Bool()),
                new Array(new Bytes())
        };
    }


}
