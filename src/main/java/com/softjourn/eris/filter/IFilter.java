package com.softjourn.eris.filter;

import java.util.Map;

/**
 * Generalize the filter that send to RPC calls
 * Created by vromanchuk on 19.01.17.
 */
public interface IFilter {
    Map<String, Object> getMap();
}
