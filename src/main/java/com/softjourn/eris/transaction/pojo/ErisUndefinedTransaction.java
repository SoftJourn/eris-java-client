package com.softjourn.eris.transaction.pojo;

import com.softjourn.eris.block.pojo.BlockHeader;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ErisUndefinedTransaction extends ErisTransaction {
    private Object body;

    private ErisUndefinedTransaction(BlockHeader blockHeader){
        super(ErisTransactionType.UNDEFINED, blockHeader);
    }

    public ErisUndefinedTransaction(Object body){
        this(null);
        this.body = body;
    }

    @Builder
    public ErisUndefinedTransaction(Object body, BlockHeader blockHeader) {
        this(blockHeader);
        this.body = body;
    }

}
