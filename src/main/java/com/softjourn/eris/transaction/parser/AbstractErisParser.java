package com.softjourn.eris.transaction.parser;


import com.softjourn.eris.block.pojo.BlockHeader;
import com.softjourn.eris.transaction.pojo.ErisTransaction;
import com.softjourn.eris.transaction.pojo.ErisUndefinedTransaction;
import com.softjourn.eris.transaction.pojo.NotValidTransactionException;

public abstract class AbstractErisParser implements ErisParser{

    public ErisTransaction parse(Object transactionBody) throws NotValidTransactionException {
        return parse(new ErisUndefinedTransaction(transactionBody));
    }

    public ErisTransaction parse(Object transactionBody, BlockHeader header) throws NotValidTransactionException {
        return parse(new ErisUndefinedTransaction(transactionBody,header));
    }
}
