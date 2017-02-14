package com.softjourn.eris.transaction.pojo;

import com.softjourn.eris.block.pojo.BlockHeader;
import lombok.Data;

/**
 * Transaction that stores at block.
 * <p>It can be encrypted in different way depending on ABI file that described contract</p>
 */
@Data
public abstract class ErisTransaction {

    public final ErisTransactionType transactionType;
    protected BlockHeader blockHeader;

    ErisTransaction(ErisTransactionType type){
        this.transactionType = type;
    }
}
