package com.softjourn.eris.contract.response;

import lombok.Data;

import java.math.BigInteger;

@Data
public class CallData {

    private String caller;

    private String callee;

    private String data;

    private BigInteger value;

    private BigInteger gas;

}
