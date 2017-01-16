package com.softjourn.eris.rpc;

/**
 * ErisRPCMethod list of rpc methods
 * Created by vromanchuk on 16.01.17.
 */
public enum ErisRPCMethod {
    GET_BLOCK("erisdb.getBlock"),
    GET_BLOCKS("erisdb.getBlocks");
    String name;

    ErisRPCMethod(String name) {
        this.name = name;
    }
}
