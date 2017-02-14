package com.softjourn.eris.transaction.parser.v11;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softjourn.eris.transaction.pojo.ErisCallTransaction;
import com.softjourn.eris.transaction.pojo.NotValidTransactionException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import static org.junit.Assert.*;


public class Eris11CallTransactionParserTest {

    private String transactionJson;
    private Eris11CallTransactionParser parser;
    private JsonNode transactionArray;
    private Object transactionArrayList;
    private Object transactionArrayList2;

    @Test
    public void parse() throws Exception {
        ErisCallTransaction transaction = parser.parse(transactionArrayList);

        assertNotNull(transaction);
        assertNotNull(transaction.getAmount());
        assertEquals(9999L, transaction.getAmount().longValue());
        assertEquals("ED7FE795D8A0B9DA6921EDBF2EEFB6E04BA76CB23B38BC43B846E77C3EA22AC8", transaction.getCallerPubKey());
        assertEquals("1301C446A0B00EF431BBBB55D09D6C41A8CF7E5AADF4C9394194731FBD96F882098A5170D5A2C33458279817D685600E842519FEC72D1BB065606F04AA1CE304", transaction.getSignature());
        assertTrue(transaction.getIsDeploy());
    }

    @Test
    public void parseWithoutPubKey() throws Exception {
        ErisCallTransaction transaction = parser.parse(transactionArrayList2);

        assertNotNull(transaction);
        assertNotNull(transaction.getAmount());
        assertEquals(1L, transaction.getAmount().longValue());
        assertFalse(transaction.getIsDeploy());
    }

    @Test(expected = NotValidTransactionException.class)
    public void parse_String_NotValidTransactionException() throws Exception {
        parser.parse(transactionJson);
    }

    @Before
    public void setUp() throws Exception {
        parser = new Eris11CallTransactionParser();
        ObjectMapper objectMapper = new ObjectMapper();

        File file;
        String root = "src/test/resources/";
        file = new File(root + "json/v11/transaction62.json");
        transactionJson = new Scanner(file).useDelimiter("\\Z").next();

        transactionArray = objectMapper.readTree(transactionJson);

        transactionArrayList = objectMapper.readValue(file, Object.class);

        transactionArrayList2 = objectMapper.readValue(new File(root + "json/v11/txWithoutPubKey.json"), Object.class);
    }
}