package com.softjourn.eris.contract.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeployResult {

    private CallData call_data;

    private String origin;

    private String tx_id;

    @JsonProperty(value = "return")
    private String compiledData;

    private String exception;

}
