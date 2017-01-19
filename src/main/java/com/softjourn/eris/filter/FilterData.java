package com.softjourn.eris.filter;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by vromanchuk on 19.01.17.
 */
@Data
@AllArgsConstructor
public class FilterData {

    private String field;
    private Operation op;
    private String value;

}
