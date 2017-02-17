package com.softjourn.eris.transaction.parser.v11;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softjourn.eris.transaction.pojo.ErisTransactionType;
import com.softjourn.eris.transaction.pojo.ErisUndefinedTransaction;
import org.junit.Before;
import org.junit.Test;

import static com.softjourn.eris.TestUtil.getStringFromFile;
import static org.junit.Assert.assertEquals;


public class Eris11ParserServiceTest {

    private Eris11ParserService service11;
    private Object transactionNodes;

    @Test
    public void defineType() throws Exception {
        assertEquals(ErisTransactionType.CALL, service11.defineType(transactionNodes));
    }

    @Test
    public void parse() throws Exception {
        service11.parse(transactionNodes);
    }

    @Before
    public void setUp() throws Exception {
        String transaction = getStringFromFile("json/v11/transaction62.json");

        ObjectMapper objectMapper = new ObjectMapper();
        transactionNodes = objectMapper.readTree(transaction);

        Eris11CallTransactionParser parser11 = new Eris11CallTransactionParser();
        service11 = new Eris11ParserService(parser11);
    }

}