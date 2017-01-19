package com.softjourn.eris.filter;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vromanchuk on 19.01.17.
 */
@Data
@AllArgsConstructor
public class FilterData implements IFilter {

    private String field;
    private Operation op;
    private String value;

    @Override
    public Map<String, Object> getMap() {
        Map<String, Object> param = new HashMap<>();
        Field[] fields = this.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(f -> {
            try {
                param.put(f.getName(), f.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return param;
    }
}
