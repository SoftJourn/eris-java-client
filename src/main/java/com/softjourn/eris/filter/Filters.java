package com.softjourn.eris.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** List of filters
 * Created by vromanchuk on 19.01.17.
 */
public class Filters {
    private List<FilterData> filters = new ArrayList<>();

    public boolean add(FilterData iFilter) {
        return filters.add(iFilter);
    }

    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("filters", this.filters);
        return map;
    }
}
