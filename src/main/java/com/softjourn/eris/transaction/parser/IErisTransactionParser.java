package com.softjourn.eris.transaction.parser;

import com.softjourn.eris.transaction.pojo.ErisTransaction;
import com.softjourn.eris.transaction.pojo.NotValidTransactionException;

public interface IErisTransactionParser {
    ErisTransaction parse(Object inputString) throws NotValidTransactionException ;
}
