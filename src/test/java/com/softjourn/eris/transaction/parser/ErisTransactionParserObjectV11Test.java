package com.softjourn.eris.transaction.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softjourn.eris.transaction.pojo.ErisTransaction;
import com.softjourn.eris.transaction.pojo.NotValidTransactionException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import static org.junit.Assert.*;


public class ErisTransactionParserObjectV11Test {

    private String transactionJson;
    private ErisTransactionParserObjectV11 parser;
    private JsonNode transactionArray;

    @Test
    public void parse() throws Exception {
        ErisTransaction transaction = parser.parse(transactionArray);
        assertNotNull(transaction);
        assertNotNull(transaction.getAmount());
        assertEquals(9999L,transaction.getAmount().longValue());
        assertTrue(transaction.getIsDeploy());
    }

    @Test(expected = NotValidTransactionException.class)
    public void parse_String_NotValidTransactionException() throws Exception {
        parser.parse(transactionJson);
    }

    @Before
    public void setUp() throws Exception {
        parser = new ErisTransactionParserObjectV11();
        ObjectMapper objectMapper = new ObjectMapper();

        File file;
        file = new File("src/test/resources/json/v11/transaction62.json");
        transactionJson = new Scanner(file).useDelimiter("\\Z").next();

        transactionArray = objectMapper.readTree(transactionJson);

    }
}