package com.softjourn.eris.rpc;

/**
 * RPCMethod list of rpc methods
 * Created by vromanchuk on 16.01.17.
 */
public enum RPCMethod {
    GET_BLOCK("erisdb.getBlock"),
    GET_BLOCKS("erisdb.getBlocks");
    String name;

    RPCMethod(String name) {
        this.name = name;
    }
}
