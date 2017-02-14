package com.softjourn.eris.transaction;

import com.softjourn.eris.block.pojo.ErisBlock;

public interface TransactionService {

    void visitTransactions(ErisBlock block);

}
