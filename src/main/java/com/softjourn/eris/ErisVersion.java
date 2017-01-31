package com.softjourn.eris;

import com.softjourn.eris.transaction.parser.ErisTransactionParserObjectV11;
import com.softjourn.eris.transaction.parser.ErisTransactionParserStringV12;
import com.softjourn.eris.transaction.parser.IErisTransactionParser;

/** Supported eris versions list
 * Created by vromanchuk on 31.01.17.
 */
public enum ErisVersion {
    V11(new ErisTransactionParserStringV12())
    ,V12(new ErisTransactionParserObjectV11());

    public IErisTransactionParser transactionParser;

    ErisVersion(IErisTransactionParser transactionParser) {
        this.transactionParser=transactionParser;
    }
}
