package com.softjourn.eris.filter;

import java.util.List;

/**
 * Created by vromanchuk on 19.01.17.
 */
public class Filters {
    private List<IFilter> filters;

    public boolean add(IFilter iFilter) {
        return filters.add(iFilter);
    }
}
