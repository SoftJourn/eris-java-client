package com.softjourn.eris.contract.response;


import lombok.Data;

import java.util.List;

@Data
public class Response {

    private final String id;

    private final List<Object> returnValues;

    private final Error error;

    private final TxParams txParams;

}
