package com.softjourn.eris.rpc;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ErisRPCError returns with rpc call to Eris
 * Created by vromanchuk on 17.01.17.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ErisRPCError extends RuntimeException {
    private Integer code;
    private String message;
}
