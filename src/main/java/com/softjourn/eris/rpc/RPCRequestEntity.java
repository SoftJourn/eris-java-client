package com.softjourn.eris.rpc;

public interface RPCRequestEntity {

    /**
     * Should return new RPCRequestEntity with new id.
     * Other parts should not be changed.
     * @param id id to set
     * @return new RPCRequestEntity
     */
    RPCRequestEntity setId(String id);

    /**
     * Should return JSON representation of this object.
     * id, jsonrpc, method, parms is mandatory fields
     * @return String as SON representation of this object
     */
    String toString();
}
