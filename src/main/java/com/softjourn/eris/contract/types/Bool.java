package com.softjourn.eris.contract.types;


public class Bool extends Type<Boolean> {

    @Override
    public String formatInput(Boolean value) {
        return "000000000000000000000000000000000000000000000000000000000000000" + (value ? "1" : "0");
    }

    @Override
    public Boolean formatOutput(String value) {
        if (canRepresent(value))
            return value.equals("0000000000000000000000000000000000000000000000000000000000000001");
        else
            throw new IllegalArgumentException("Type bool can't represent value " + value + " as boolean.");
    }

    @Override
    public boolean canRepresent(String value) {
        return value!= null && value.matches("0{63}[01]");
    }

    @Override
    public String toString() {
        return "bool";
    }

    @Override
    public Class<Boolean> valueClass() {
        return Boolean.class;
    }

    public static boolean isType(String name) {
        return name.matches("bool");
    }
}
