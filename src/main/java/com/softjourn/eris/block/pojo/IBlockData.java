package com.softjourn.eris.block.pojo;

import com.softjourn.eris.transaction.pojo.ClassifiableErisTransaction;

import java.util.List;

/**
 * Structure that contains transactions in block.
 * <p>Can be different depends on Eris version</p>
 * @author vromanchuk
 * @since 0.9.3
 */
interface IBlockData {
    List<ClassifiableErisTransaction> getTransactions();
}
