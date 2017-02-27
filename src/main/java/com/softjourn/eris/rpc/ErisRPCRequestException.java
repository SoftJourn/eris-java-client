package com.softjourn.eris.rpc;

public class ErisRPCRequestException extends RuntimeException {

    private final RPCRequestEntity entity;

    ErisRPCRequestException(String s, RPCRequestEntity entity, Throwable throwable) {
        super(s, throwable);
        this.entity = entity;
    }

    public RPCRequestEntity getEntity() {
        return entity;
    }
}
