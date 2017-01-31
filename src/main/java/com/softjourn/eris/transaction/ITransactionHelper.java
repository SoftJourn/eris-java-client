package com.softjourn.eris.transaction;

import com.softjourn.eris.rpc.ErisRPCError;
import com.softjourn.eris.transaction.pojo.Block;
import com.softjourn.eris.transaction.pojo.BlockMeta;

import java.util.List;
import java.util.stream.Stream;

public interface ITransactionHelper {
    Long MAX_BLOCKS_PER_REQUEST = new Long("51");

    Block getBlock(Long blockNumber) throws ErisRPCError;

    String getBlockJSON(Long blockNumber);

    Long getLatestBlockNumber();

    Stream<BlockMeta> getBlockStream(Long from, Long to);

    List<BlockMeta> getBlocks(Long from, Long to);
}
