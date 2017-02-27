package com.softjourn.eris.block;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softjourn.eris.block.pojo.ErisBlock;
import com.softjourn.eris.block.pojo.ErisBlocks;
import com.softjourn.eris.block.pojo.Height;
import com.softjourn.eris.filter.FilterData;
import com.softjourn.eris.filter.Filters;
import com.softjourn.eris.filter.Operation;
import com.softjourn.eris.filter.type.FilterHeight;
import com.softjourn.eris.rpc.*;
import com.softjourn.eris.transaction.TransactionService;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Manage data in block chain stored data
 * Created by vromanchuk on 10.02.17.
 */
public class ErisBlockChainService implements BlockChainService {

    private final static int DEFAULT_MAX_CALL_ATTEMPTS = 3;
    private final static int DEFAULT_DELAY = 500;

    private final HTTPRPCClient httpRpcClient;
    private TransactionService transactionService;
    private int maxCallAttempts;
    private int delayTime;


    @Override
    public Long getLatestBlockNumber() {
        ErisRPCRequestEntity entity = new ErisRPCRequestEntity(null, RPCMethod.GET_LATEST_BLOCK);
        String json = call(entity);
        ErisRPCResponseEntity<Height> response = new ErisRPCResponseEntity<>(json, Height.class);
        return response.getResult().getHeight();
    }

    @Override
    public void visitTransactionsFromBlock(long blockHeight) {
        ErisBlock block = this.getBlock(blockHeight);
        transactionService.visitTransactions(block);
    }

    @Override
    public void visitTransactionsFromBlocks(long fromBlockHeight, long toBlockHeight) {
        this.getBlocksWithTransactions(fromBlockHeight,toBlockHeight)
                .forEach( block -> transactionService.visitTransactions(block));
    }


    public ErisBlockChainService(String host, TransactionService transactionService) {
        this.httpRpcClient = new HTTPRPCClient(host);
        this.transactionService = transactionService;
        this.setDelayTime(DEFAULT_DELAY);
        this.setMaxCallAttempts(DEFAULT_MAX_CALL_ATTEMPTS);
    }

    ErisBlock getBlock(long blockNumber) throws ErisRPCError {
        String json = this.getBlockJson(blockNumber);
        ErisRPCResponseEntity<ErisBlock> response = new ErisRPCResponseEntity<>(json, ErisBlock.class);
        if (response.getError() != null) {
            throw response.getError();
        } else {
            return response.getResult();
        }
    }

    Stream<ErisBlock> getBlocksWithTransactions(long from, long to) {
        return getBlockNumbersWithTx(from, to)
                .map(this::getBlock);
    }

    String getBlockJson(long blockNumber) {
        Height height = new Height(blockNumber);
        ErisRPCRequestEntity entity = new ErisRPCRequestEntity(height.getParams(), RPCMethod.GET_BLOCK);
        return call(entity);
    }

    Stream<Long> getBlockNumbersWithTx(long from, final long to) {
        this.validateGetBlocksParams(from, to);
        return Stream.iterate(from, i -> i + MAX_BLOCKS_PER_REQUEST)
                .limit(calculateLimit(from, to))
                .map(curFrom -> getBlocksRequestEntity(curFrom, getMiddleNumber(curFrom, to)))
                .map(this::call)
                .map(resultJSON -> new ErisRPCResponseEntity<>(resultJSON, ErisBlocks.class))
                .map(ErisRPCResponseEntity::getResult)
                .peek(res -> delayRequests())
                .flatMap(blocks -> blocks.getBlockNumbersWithTransaction());
    }

    private synchronized String call(ErisRPCRequestEntity erisRPCRequestEntity){
        int attempts = 0;
        ErisRPCRequestException exception;
        do {
            try {
                return httpRpcClient.call(erisRPCRequestEntity);
            } catch (ErisRPCRequestException e) {
                attempts++;
                exception = e;
                delayRequests();
            }
        }while (attempts < maxCallAttempts);
        throw exception;
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

    private void delayRequests() {
        try {
            Thread.sleep(delayTime);
        } catch (InterruptedException e) {
            throw new RuntimeException("Execution of getting blocks was interrupted.", e);
        }
    }

    /**
     * Number of tries to make a call before exception is thrown
     * <p>Default value is 3 </p>
     * @param maxCallAttempts max value of tries
     */
    public void setMaxCallAttempts(int maxCallAttempts) {
        if(maxCallAttempts > 1)
            this.maxCallAttempts = maxCallAttempts;
        else
            throw new IllegalArgumentException("Max call attempts value can't be less than 1");
    }

    /**
     * Time to wait after every call to Eris block chain
     * <p>Default value is 500</p>
     * @param delayTime milli seconds
     */
    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }
}
