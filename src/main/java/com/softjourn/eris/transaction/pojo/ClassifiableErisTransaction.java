package com.softjourn.eris.transaction.pojo;

import lombok.Data;

/**
 * Transaction that stores at block.
 * <p>It can be encrypted in different way depending on ABI file that described contract</p>
 */
@Data
public class ClassifiableErisTransaction {
    protected ErisTransactionType transactionType = ErisTransactionType.UNDEFINED;
    protected Object transaction;
}
