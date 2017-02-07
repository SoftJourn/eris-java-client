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
    private Object transactionArrayList;
    private Object transactionArrayList2;

    @Test
    public void parse() throws Exception {
        ErisTransaction transaction = parser.parse(transactionArrayList);

        assertNotNull(transaction);
        assertNotNull(transaction.getAmount());
        assertEquals(9999L,transaction.getAmount().longValue());
        assertTrue(transaction.getIsDeploy());
    }

    @Test
    public void parseWithoutPubKey() throws Exception {
        ErisTransaction transaction = parser.parse(transactionArrayList2);

        assertNotNull(transaction);
        assertNotNull(transaction.getAmount());
        assertEquals(1L,transaction.getAmount().longValue());
        assertFalse(transaction.getIsDeploy());
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

        transactionArrayList = objectMapper.readValue(file,Object.class);

        transactionArrayList2 = objectMapper.readValue(new File("src/test/resources/json/v11/txWithoutPubKey.json"),Object.class);
    }
}