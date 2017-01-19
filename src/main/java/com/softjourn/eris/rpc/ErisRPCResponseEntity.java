package com.softjourn.eris.rpc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;

/**
 * ErisRPCResponseEntity
 * Created by vromanchuk on 16.01.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ErisRPCResponseEntity<T> {

    @JsonIgnore
    private static ObjectMapper objectMapper = new ObjectMapper();

    private T result;
    private ErisRPCError error;
    private String id;
    @JsonProperty(value = "jsonrpc")
    private String jsonRpc;

    @SuppressWarnings("unused")
    private ErisRPCResponseEntity() {

    }

    public ErisRPCResponseEntity(String json, Class<?> result) throws IOException {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ErisRPCResponseEntity.class, result);
//        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SnakeCaseStrategy.SNAKE_CASE);
        ErisRPCResponseEntity<T> object = objectMapper.readValue(json, javaType);

        this.result = object.result;
        this.error = object.error;
        this.id = object.id;
        this.jsonRpc = object.jsonRpc;
    }

}
