package com.softjourn.eris.rpc;

/**
 * RPCMethod list of rpc methods
 * https://monax.io/docs/documentation/db/latest/specifications/api/#methods
 *
 * GET_BLOCK("erisdb.getBlock")
 * param Height.class
 *
 * GET_LATEST_BLOCK("erisdb.getLatestBlockHeight")
 * no param
 *
 * GET_BLOCKS("erisdb.getBlocks")
 * param Filters
 *
 * CONSTANT_CALL("erisdb.call")
 * param
 *      address: <string>
 *      data: <string>
 * return
 *      return:   <string>
 *      gas_used: <number>
 *
 * TRANSACTIONAL_CALL("erisdb.transactAndHold")
 * TODO this value should be changed when Eris will start supporting signing
 * see https://docs.erisindustries.com/documentation/eris-db-api/#unsafe
 *
 * SEND_CALL("erisdb.sendAndHold")
 * TODO this value should be changed when Eris will start supporting signing
 * see https://docs.erisindustries.com/documentation/eris-db-api/#unsafe
 *
 */
public enum RPCMethod {
    GET_BLOCK("erisdb.getBlock"),
    GET_LATEST_BLOCK("erisdb.getLatestBlockHeight"),
    GET_BLOCKS("erisdb.getBlocks"),
    CONSTANT_CALL("erisdb.call"),
    TRANSACTIONAL_CALL("erisdb.transactAndHold"),
    SEND_CALL("erisdb.sendAndHold");

    String name;

    RPCMethod(String name) {
        this.name = name;
    }
}
