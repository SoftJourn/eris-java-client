package com.softjourn.eris.transaction.pojo;

import com.softjourn.eris.transaction.parser.ErisTransactionParserObjectV11;
import com.softjourn.eris.transaction.parser.ErisTransactionParserStringV12;

public class ErisTransactionCreator {

    private static ErisTransactionParserStringV12 binaryParser = new ErisTransactionParserStringV12();
    private static ErisTransactionParserObjectV11 jsonParser = new ErisTransactionParserObjectV11();

    public static ErisTransaction create(Object input) throws NotValidTransactionException {
        if(input instanceof String)
            return binaryParser.parse(input);
        else
            return jsonParser.parse(input);
    }
}
