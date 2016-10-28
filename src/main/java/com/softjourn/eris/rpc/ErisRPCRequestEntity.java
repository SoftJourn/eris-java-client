package com.softjourn.eris.rpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Getter;

import java.util.Map;

/**
 * RPC entity to call eris contract
 */
@Getter
public class ErisRPCRequestEntity {

    private static final String CONSTANT_CALL_METHOD = "erisdb.call";

    //TODO this value should be changed when Eris will start supporting signing
    // see https://docs.erisindustries.com/documentation/eris-db-api/#unsafe
    private static final String TRANSACTIONAL_CALL_METHOD = "erisdb.transactAndHold";

    //TODO this value should be changed when Eris will start supporting signing
    // see https://docs.erisindustries.com/documentation/eris-db-api/#unsafe
    private static final String SEND_CALL_METHOD = "erisdb.sendAndHold";

    private String jsonrpc = "2.0";

    private String method;

    private String id;

    private Map<String, Object> params;

    public ErisRPCRequestEntity(Map<String, Object> params, String method) {
        this(params, method, "");
    }

    public ErisRPCRequestEntity(Map<String, Object> params, String method, String id) {
        this.params = params;
        this.method = method;
        this.id = id;
    }

    /**
     * Creates entity to call method that not requires transaction
     * @param params params to pass to contract method
     * @return RequestEntity
     */
    public static ErisRPCRequestEntity constantCallEntity(Map<String, Object> params) {
        return new ErisRPCRequestEntity(params, CONSTANT_CALL_METHOD);
    }

    /**
     * Creates entity to call method that requires transaction
     * @param params params to pass to contract method
     * @return RequestEntity
     */
    public static ErisRPCRequestEntity transactionalCallEntity(Map<String, Object> params) {
        return new ErisRPCRequestEntity(params, TRANSACTIONAL_CALL_METHOD);
    }

    /**
     * Creates entity to call method that requires transaction
     * @param params params to pass to contract method
     * @return RequestEntity
     */
    public static ErisRPCRequestEntity sendCallEntity(Map<String, Object> params) {
        return new ErisRPCRequestEntity(params, SEND_CALL_METHOD);
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
