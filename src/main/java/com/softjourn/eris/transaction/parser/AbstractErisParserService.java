package com.softjourn.eris.transaction.parser;

import com.softjourn.eris.transaction.pojo.ErisTransaction;
import com.softjourn.eris.transaction.pojo.ErisTransactionType;
import com.softjourn.eris.transaction.pojo.NotValidTransactionException;

import java.util.Arrays;

public abstract class AbstractErisParserService implements ErisParserService {

    private final ErisParser[] parsers;

    private ErisParser getParser(ErisTransactionType type) {
        return Arrays.stream(parsers)
                .filter(erisParser -> erisParser.getTransactionType() == type)
                .findFirst()
                .orElse(null);
    }

    public ErisTransaction parse(Object input) throws NotValidTransactionException {
        ErisTransactionType type = defineType(input);
        ErisParser parser = getParser(type);
        if (parser == null) {
            throw new NotValidTransactionException("Parser is not defined for this type of transaction "+type.name());
        }
        return parser.parse(input);
    }

    protected AbstractErisParserService(ErisParser... parsers) {
        this.parsers = parsers;
    }
}
