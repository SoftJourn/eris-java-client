package com.softjourn.eris.contract.response;

import com.softjourn.eris.contract.Contract;
import lombok.Data;

@Data
public class DeployResponse {

    private final String id;

    private final DeployResult result;

    private final Error error;

    private final String jsonrpc;

    private Contract contract;

}
