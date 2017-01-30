package com.softjourn.eris.filter.type;

import com.softjourn.eris.filter.FilterData;
import com.softjourn.eris.filter.Operation;


public class FilterHeight extends FilterData {
    public FilterHeight(Operation op, Long value) {
        super("height", op, value.toString());
    }
}
