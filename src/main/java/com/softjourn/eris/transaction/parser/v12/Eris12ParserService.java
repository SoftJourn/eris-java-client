package com.softjourn.eris.transaction.parser.v12;

import com.softjourn.eris.transaction.parser.AbstractErisParserService;
import com.softjourn.eris.transaction.parser.ErisParser;
import com.softjourn.eris.transaction.pojo.ErisTransactionType;

public class Eris12ParserService extends AbstractErisParserService {

    private static final Integer HEX_BASE = 16;

    @Override
    public ErisTransactionType defineType(Object transactionBody) {
        int code = 0;
        if(transactionBody instanceof String){
            String txString = (String) transactionBody;
            code = Integer.valueOf(txString.substring(0,2),HEX_BASE);
        }
        return ErisTransactionType.findByCode(code);
    }

    public Eris12ParserService(ErisParser ... parsers) {
        super(parsers);
    }
}
