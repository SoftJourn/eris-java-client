package com.softjourn.eris.contract.response;


import lombok.Data;

import java.lang.*;

@Data
public class Response<T> {

    private final String id;

    private final ReturnValue<T> returnValue;

    private final Error error;

    private final TxParams txParams;

}
