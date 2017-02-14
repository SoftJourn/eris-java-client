package com.softjourn.eris.transaction.pojo;

import com.softjourn.eris.block.pojo.BlockHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ErisUndefinedTransaction extends ErisTransaction {
    private Object transaction;

    private ErisUndefinedTransaction(){
        super(ErisTransactionType.UNDEFINED);
    }

    public ErisUndefinedTransaction(Object transaction, BlockHeader blockHeader) {
        this(transaction);
        this.blockHeader = blockHeader;
    }

    private ErisUndefinedTransaction(Object transaction) {
        this();
        this.transaction = transaction;
    }
}
