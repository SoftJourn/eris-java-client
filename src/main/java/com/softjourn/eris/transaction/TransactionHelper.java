package com.softjourn.eris.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softjourn.eris.filter.FilterData;
import com.softjourn.eris.filter.Filters;
import com.softjourn.eris.filter.Operation;
import com.softjourn.eris.filter.type.FilterHeight;
import com.softjourn.eris.rpc.*;
import com.softjourn.eris.transaction.type.Block;
import com.softjourn.eris.transaction.type.BlockMeta;
import com.softjourn.eris.transaction.type.Blocks;
import com.softjourn.eris.transaction.type.Height;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TransactionHelper
 * Created by vromanchuk on 12.01.17.
 */
public class TransactionHelper {

    public static final Long MAX_BLOCKS_PER_REQUEST = new Long("51");
    private HTTPRPCClient httpRpcClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    public TransactionHelper(String host) {
        this.httpRpcClient = new HTTPRPCClient(host);
    }

    public Block getBlock(Long blockNumber) throws ErisRPCError {
        String blockJSON = this.getBlockJSON(blockNumber);
        ErisRPCResponseEntity<Block> response = new ErisRPCResponseEntity<>(blockJSON, Block.class);
        if (response.getError() != null) {
            throw response.getError();
        }
        return response.getResult();
    }

    public String getBlockJSON(Long blockNumber) {
        Height height = new Height(blockNumber);
        Map<String, Object> param = objectMapper.convertValue(height, Map.class);
        ErisRPCRequestEntity entity = new ErisRPCRequestEntity(param, RPCMethod.GET_BLOCK);
        return httpRpcClient.call(entity);
    }

    public Long getLatestBlockNumber() {
        ErisRPCRequestEntity entity = new ErisRPCRequestEntity(null, RPCMethod.GET_LATEST_BLOCK);
        String json = httpRpcClient.call(entity);
        ErisRPCResponseEntity<Height> response = new ErisRPCResponseEntity<>(json, Height.class);
        return response.getResult().getHeight();
    }

    private ErisRPCRequestEntity getBlocksRequestEntity(long from, long to) {
        Filters filters = new Filters();
        if (to - 1 == from) {
            FilterData filterEquals = new FilterHeight(Operation.EQUALS, from);
            filters.add(filterEquals);
        } else {
            FilterData filterFrom = new FilterHeight(Operation.GREATER_OR_EQUALS, from);
            FilterData filterTo = new FilterHeight(Operation.LESS, to);
            filters.add(filterFrom);
            filters.add(filterTo);
        }
        return new ErisRPCRequestEntity(filters.getMap(), RPCMethod.GET_BLOCKS);
    }

    private Long getMiddleNumber(long from, long maxTo) {
        return Long.min(from + MAX_BLOCKS_PER_REQUEST + 1, maxTo);
    }

    private void validateGetBlocksParams(Long from, final Long to) {
        if (from.compareTo(1L) < 0)
            throw new IllegalArgumentException("From height can't be less then 1");
        if (from.compareTo(to) >= 0)
            throw new IllegalArgumentException("From height can't be grater or equals To height");
    }

    private long calculateLimit(Long from, final Long to) {
        long temp = to - from - 1;
        long mod = temp % MAX_BLOCKS_PER_REQUEST;
        return mod > 0 ? temp / MAX_BLOCKS_PER_REQUEST + 1 : temp / MAX_BLOCKS_PER_REQUEST;
    }

    public Stream<BlockMeta> getBlockStream(Long from, final Long to) {
        this.validateGetBlocksParams(from, to);
        return Stream.iterate(from, i -> i + MAX_BLOCKS_PER_REQUEST)
                .limit(calculateLimit(from, to))
                .map(curFrom -> getBlocksRequestEntity(curFrom, getMiddleNumber(curFrom, to)))
                .map(httpRpcClient::call)
                .map(resultJSON -> new ErisRPCResponseEntity<>(resultJSON, Blocks.class))
                .map(ErisRPCResponseEntity::getResult)
                .map(Blocks::getBlockMetas)
                .flatMap(Collection::stream);
    }

    public List<BlockMeta> getBlocks(Long from, Long to) {
        return this.getBlockStream(from, to).collect(Collectors.toList());
    }
}
