package com.softjourn.eris.transaction.parser;

import com.softjourn.eris.transaction.pojo.ErisTransaction;
import com.softjourn.eris.transaction.pojo.ErisTransactionType;
import com.softjourn.eris.transaction.pojo.NotValidTransactionException;

public interface ErisParser {
    ErisTransaction parse(Object input) throws NotValidTransactionException;
    ErisTransactionType getTransactionType();
}
