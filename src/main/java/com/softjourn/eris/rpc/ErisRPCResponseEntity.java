package com.softjourn.eris.rpc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * ErisRPCResponseEntity
 * Created by vromanchuk on 16.01.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErisRPCResponseEntity {
    private Object result;

}
