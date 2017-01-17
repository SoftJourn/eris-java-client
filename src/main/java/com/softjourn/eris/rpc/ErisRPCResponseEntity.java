package com.softjourn.eris.rpc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;

/**
 * ErisRPCResponseEntity
 * Created by vromanchuk on 16.01.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ErisRPCResponseEntity {

    private Object result;
    private Error error;
    private String id;
    @JsonProperty(value = "jsonrpc")
    private String jsonRpc;

    private ErisRPCResponseEntity() {

    }

    public static ErisRPCResponseEntity getInstance(String data) throws IOException {
        System.out.println(data);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(data, ErisRPCResponseEntity.class);
    }

    @Data
    public class Error {
        private Integer code;
        private String message;
    }

}
