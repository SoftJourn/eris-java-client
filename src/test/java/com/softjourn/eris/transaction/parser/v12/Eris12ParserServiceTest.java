package com.softjourn.eris.transaction.parser.v12;

import com.softjourn.eris.transaction.parser.ErisParser;
import com.softjourn.eris.transaction.pojo.ErisCallTransaction;
import com.softjourn.eris.transaction.pojo.ErisTransactionType;
import org.junit.Before;
import org.junit.Test;

import static com.softjourn.eris.TestUtil.getStringFromFile;
import static org.junit.Assert.assertEquals;

public class Eris12ParserServiceTest {

    private Eris12ParserService parserService;
    private String transaction;

    @Test
    public void defineType() throws Exception {
        assertEquals(ErisTransactionType.CALL, parserService.defineType(transaction));
    }

    @Test
    public void parse() throws Exception {
        ErisCallTransaction callTransaction= (ErisCallTransaction) parserService.parse(transaction);
    }

    @Before
    public void setUp() throws Exception {
        ErisParser parser = new Eris12CallTransactionParser();
        parserService = new Eris12ParserService(parser);
        transaction = getStringFromFile("binary/TransactionBinary.txt");
    }
}