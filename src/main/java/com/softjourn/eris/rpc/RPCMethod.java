package com.softjourn.eris.rpc;

/**
 * RPCMethod list of rpc methods
 * https://monax.io/docs/documentation/db/latest/specifications/api/#methods
 *
 * GET_BLOCK("erisdb.getBlock")
 * param { height: <number> }
 *
 * GET_LATEST_BLOCK("erisdb.getLatestBlockHeight")
 * no param
 *
 *
 * Created by vromanchuk on 16.01.17.
 */
public enum RPCMethod {
    GET_BLOCK("erisdb.getBlock"),
    GET_LATEST_BLOCK("erisdb.getLatestBlockHeight"),
    GET_BLOCKS("erisdb.getBlocks");
    String name;

    RPCMethod(String name) {
        this.name = name;
    }
}