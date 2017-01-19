package com.softjourn.eris.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softjourn.eris.filter.FilterData;
import com.softjourn.eris.filter.Filters;
import com.softjourn.eris.filter.Operation;
import com.softjourn.eris.filter.type.FilterHeight;
import com.softjourn.eris.rpc.*;
import com.softjourn.eris.transaction.type.Block;
import com.softjourn.eris.transaction.type.Height;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * TransactionHelper
 * Created by vromanchuk on 12.01.17.
 */
public class TransactionHelper {

    private HTTPRPCClient httpRpcClient;
    private ObjectMapper objectMapper = new ObjectMapper();

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

        Height height = new Height(blockNumber);
        Map<String, Object> param = objectMapper.convertValue(height, Map.class);
        ErisRPCRequestEntity entity = new ErisRPCRequestEntity(param, RPCMethod.GET_BLOCK);
        return httpRpcClient.call(entity);
    }

    public BigInteger getLatestBlockNumber() throws IOException {
        ErisRPCRequestEntity entity = new ErisRPCRequestEntity(null, RPCMethod.GET_LATEST_BLOCK);
        String json = httpRpcClient.call(entity);
        ErisRPCResponseEntity<Height> response = new ErisRPCResponseEntity<>(json, Height.class);
        return response.getResult().getHeight();
    }

    public List<Block> getBlocks(BigInteger from, BigInteger to) {
        Filters filters = new Filters();
        FilterData filterFrom = new FilterHeight(Operation.GREATER_OR_EQUALS, from);
        FilterData filterTo = new FilterHeight(Operation.LESS_OR_EQUALS, to);
        filters.add(filterFrom);
        filters.add(filterTo);
        ErisRPCRequestEntity entity = new ErisRPCRequestEntity(filters.getMap(), RPCMethod.GET_BLOCKS);
        return null;
    }
}
