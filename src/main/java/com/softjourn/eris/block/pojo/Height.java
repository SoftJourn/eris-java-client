package com.softjourn.eris.block.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Height returns from get latest block
 * Created by vromanchuk on 17.01.17.
 */
@Data
@AllArgsConstructor
public class Height {
    private Long height;

    public Map<String, Object> getParams() {
        return new HashMap<String, Object>() {{
            put("height", height);
        }};
    }
}
