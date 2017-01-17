package com.softjourn.eris.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softjourn.eris.rpc.ErisRPCRequestEntity;
import com.softjourn.eris.rpc.ErisRPCResponseEntity;
import com.softjourn.eris.rpc.HTTPRPCClient;
import com.softjourn.eris.rpc.RPCMethod;
import com.softjourn.eris.transaction.type.Block;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * TransactionHelper
 * Created by vromanchuk on 12.01.17.
 */
public class TransactionHelper {

    private ObjectMapper objectMapper = new ObjectMapper();
    private String host;
    private HTTPRPCClient httpRpcClient;

    public TransactionHelper(String host) {
        this.host = host;
        this.httpRpcClient = new HTTPRPCClient(host);
    }

    public Block getBlock(BigInteger blockNumber) throws IOException {
        String blockJSON = this.getBlockJSON(blockNumber);
        return objectMapper.readValue(blockJSON, Block.class);
    }

    public String getBlockJSON(BigInteger blockNumber) throws IOException {
        Map<String, Object> param = new HashMap<>();
        param.put("height", blockNumber.toString());
        ErisRPCRequestEntity entity = new ErisRPCRequestEntity(param, RPCMethod.GET_BLOCK);
        ErisRPCResponseEntity response = ErisRPCResponseEntity.getInstance(httpRpcClient.call(entity));
        System.out.println(response);
        return "unused";
    }

    public Block getLatestBlock() throws IOException {
        String endpoint = "/blockchain/latest_block";
        String url = host + endpoint;
//        String blockJSON = restTemplate.getForEntity(url, String.class).getBody();
//        return objectMapper.readValue(blockJSON, Block.class);
        return null;
    }

    public BigInteger getLatestBlockNumber() throws IOException {
        Block latestBlock = this.getLatestBlock();
        return latestBlock.getHeader().getHeight();
    }
}
