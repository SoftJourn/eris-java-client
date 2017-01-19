package com.softjourn.eris.filter.type;

import com.softjourn.eris.filter.FilterData;
import com.softjourn.eris.filter.Operation;

import java.math.BigInteger;

/**
 * Created by vromanchuk on 19.01.17.
 */
public class FilterHeight extends FilterData {
    private static final String FIELD = "height";

    public FilterHeight(Operation op, BigInteger value) {
        super(FIELD, op, value.toString());
    }
}
