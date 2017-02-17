package com.softjourn.eris.transaction.parser;

import com.softjourn.eris.transaction.pojo.ErisTransaction;
import com.softjourn.eris.transaction.pojo.ErisTransactionType;
import com.softjourn.eris.transaction.pojo.NotValidTransactionException;


public interface ErisParserService {
    ErisTransactionType defineType(Object transactionBody);
    ErisTransaction parse(Object transaction) throws NotValidTransactionException;
}
