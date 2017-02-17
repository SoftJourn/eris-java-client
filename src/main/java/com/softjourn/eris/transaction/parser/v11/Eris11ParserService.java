package com.softjourn.eris.transaction.parser.v11;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.softjourn.eris.transaction.parser.AbstractErisParserService;
import com.softjourn.eris.transaction.parser.ErisParser;
import com.softjourn.eris.transaction.pojo.ErisTransactionType;
import com.softjourn.eris.transaction.pojo.ErisUndefinedTransaction;

import java.util.ArrayList;

public class Eris11ParserService extends AbstractErisParserService {

    public ErisTransactionType defineType(Object inputArray) {
        int code = 0;
        ErisTransactionType transactionType;
        try {
            if (inputArray instanceof ArrayList) {
                ArrayList list = (ArrayList) inputArray;
                code = (int) list.get(0);
            }
            if (inputArray instanceof ArrayNode) {
                ArrayNode node = (ArrayNode) inputArray;
                code = node.get(0).asInt();
            }
        } finally {
            transactionType = ErisTransactionType.findByCode(code);
        }
        return transactionType;
    }

    public Eris11ParserService(ErisParser... parsers) {
        super(parsers);
    }

}
