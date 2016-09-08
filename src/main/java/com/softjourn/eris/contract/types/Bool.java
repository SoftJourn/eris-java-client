package com.softjourn.eris.contract.types;


public class Bool extends Type<Boolean> {

    @Override
    public String formatInput(Boolean value) {
        return "000000000000000000000000000000000000000000000000000000000000000" + (value ? "1" : "0");
    }

    @Override
    public Boolean formatOutput(String value) {
        return value.equals("0000000000000000000000000000000000000000000000000000000000000001");
    }

    @Override
    public boolean canRepresent(Object value) {
        return value instanceof Boolean;
    }

    @Override
    public String toString() {
        return "bool";
    }

    @Override
    public Class<Boolean> valueClass() {
        return Boolean.class;
    }
}
