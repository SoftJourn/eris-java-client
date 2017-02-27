package com.softjourn.eris.rpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Map;

/**
 * RPC entity to call eris contract
 */
@Getter
@EqualsAndHashCode
public class ErisRPCRequestEntity implements RPCRequestEntity {

    private String jsonrpc = "2.0";

    private String method;

    private String id;

    private Map<String, Object> params;

    public ErisRPCRequestEntity(Map<String, Object> params, RPCMethod method) {
        this(params, method.name, "");
    }

    public ErisRPCRequestEntity(Map<String, Object> params, String method) {
        this(params, method, "");
    }

    public ErisRPCRequestEntity(Map<String, Object> params, String method, String id) {
        this.params = params;
        this.method = method;
        this.id = id;
    }

    /**
     * Creates entity to call method that not requires body
     * @param params params to pass to contract method
     * @return RequestEntity
     */
    public static ErisRPCRequestEntity constantCallEntity(Map<String, Object> params) {
        return new ErisRPCRequestEntity(params, RPCMethod.CONSTANT_CALL);
    }

    /**
     * Creates entity to call method that requires body
     * @param params params to pass to contract method
     * @return RequestEntity
     */
    public static ErisRPCRequestEntity transactionalCallEntity(Map<String, Object> params) {
        return new ErisRPCRequestEntity(params, RPCMethod.TRANSACTIONAL_CALL);
    }

    /**
     * Creates entity to call method that requires body
     * @param params params to pass to contract method
     * @return RequestEntity
     */
    public static ErisRPCRequestEntity sendCallEntity(Map<String, Object> params) {
        return new ErisRPCRequestEntity(params, RPCMethod.SEND_CALL);
    }

    public ErisRPCRequestEntity setId(String id) {
        return new ErisRPCRequestEntity(params, method, id);
    }

    @Override
    public String toString() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writer();
            return writer.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            //should newer happened
            throw new RuntimeException("Can't write request entity as JSON.", e);
        }
    }
}
