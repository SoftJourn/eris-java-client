package com.softjourn.eris.filter.type;

import com.softjourn.eris.filter.FilterData;
import com.softjourn.eris.filter.Operation;

import java.math.BigInteger;


public class FilterHeight extends FilterData {
    public FilterHeight(Operation op, BigInteger value) {
        super("height", op, value.toString());
    }
}
