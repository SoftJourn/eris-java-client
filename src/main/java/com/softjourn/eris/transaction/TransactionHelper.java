package com.softjourn.eris.transaction;

import com.softjourn.eris.rpc.*;
import com.softjourn.eris.transaction.type.Block;
import com.softjourn.eris.transaction.type.Height;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * TransactionHelper
 * Created by vromanchuk on 12.01.17.
 */
public class TransactionHelper {

    private HTTPRPCClient httpRpcClient;

    public TransactionHelper(String host) {
        this.httpRpcClient = new HTTPRPCClient(host);
    }

    public Block getBlock(BigInteger blockNumber) throws IOException, ErisRPCError {
        String blockJSON = this.getBlockJSON(blockNumber);
        ErisRPCResponseEntity<Block> response = new ErisRPCResponseEntity<>(blockJSON, Block.class);
        if (response.getError() != null) {
            throw response.getError();
        }
        return response.getResult();
    }

    public String getBlockJSON(BigInteger blockNumber) throws IOException {
        Map<String, Object> param = new HashMap<>();
        param.put("height", blockNumber);
        ErisRPCRequestEntity entity = new ErisRPCRequestEntity(param, RPCMethod.GET_BLOCK);
        return httpRpcClient.call(entity);
    }

    public BigInteger getLatestBlockNumber() throws IOException {
        ErisRPCRequestEntity entity = new ErisRPCRequestEntity(null, RPCMethod.GET_LATEST_BLOCK);
        String json = httpRpcClient.call(entity);
        ErisRPCResponseEntity<Height> response = new ErisRPCResponseEntity<>(json, Height.class);
        return response.getResult().getHeight();
    }
}
