package com.softjourn.eris.block;

public interface BlockChainService {

    Long MAX_BLOCKS_PER_REQUEST = new Long("51");

    Long getLatestBlockNumber();

    void visitTransactionsFromBlock(long blockHeight);

    void visitTransactionsFromBlocks(long fromBlockHeight, long toBlockHeight);
}
