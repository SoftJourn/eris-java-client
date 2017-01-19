package com.softjourn.eris.filter;

/**
 * Operations in filters
 * Created by vromanchuk on 19.01.17.
 */
public enum Operation {
    GREATER(">"),
    GREATER_OR_EQUALS(">="),
    EQUALS("=="),
    LESS_OR_EQUALS("<="),
    LESS("<");
    private String val;

    Operation(String s) {
        val = s;
    }

    @Override
    public String toString() {
        return this.val;
    }
}
