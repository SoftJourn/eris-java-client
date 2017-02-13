package com.softjourn.eris.block;

import com.softjourn.eris.block.pojo.Block;
import com.softjourn.eris.rpc.ErisRPCError;

import java.util.stream.Stream;

public interface BlockChainService {

    Long MAX_BLOCKS_PER_REQUEST = new Long("51");

    Block getBlock(long blockNumber) throws ErisRPCError;

    Long getLatestBlockNumber();

    Stream<Block> getBlocksWithTransactions(long from, long to);

}
