package com.softjourn.eris.block.pojo;

import java.time.LocalDateTime;

/**
 * This is main particle of block chain.
 * <p>
 * Eris implementation includes data, like
 * <ul>
 * <li>Block number (ID)</li>
 * <li>Chain name</li>
 * <li>Generation time</li>
 * <li>Transactions</li>
 * </ul></p>
 */
public interface IBlock extends IBlockData{
    Long getBlockNumber();
    String getChainName();
    LocalDateTime getTimeCreated();
}
