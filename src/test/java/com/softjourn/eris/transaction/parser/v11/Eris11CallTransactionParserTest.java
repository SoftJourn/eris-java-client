package com.softjourn.eris.transaction.parser.v11;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softjourn.eris.block.pojo.ErisBlock;
import com.softjourn.eris.rpc.ErisRPCResponseEntity;
import com.softjourn.eris.transaction.pojo.ErisCallTransaction;
import com.softjourn.eris.transaction.pojo.ErisUndefinedTransaction;
import com.softjourn.eris.transaction.pojo.NotValidTransactionException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static com.softjourn.eris.TestUtil.getStringFromFile;
import static org.junit.Assert.*;


public class Eris11CallTransactionParserTest {

    private final static String root = "src/test/resources/";

    private String transactionJson;
    private Eris11CallTransactionParser parser;
    private JsonNode transactionArrayNode;
    private Object transactionArrayList;
    private Object txWithoutPubKey;
    private ErisUndefinedTransaction tx3424287;
    private String tx3424287Id;
    private ErisUndefinedTransaction tx3772584FirstTransaction;
    private ErisUndefinedTransaction tx3772584SecondTransaction;
    private String tx3772584FirstTransactionId;
    private String tx3772584SecondTransactionId;

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
        ErisCallTransaction transaction = parser.parse(txWithoutPubKey);

        assertNotNull(transaction);
        assertNotNull(transaction.getAmount());
        assertEquals(1L, transaction.getAmount().longValue());
        assertFalse(transaction.getIsDeploy());
    }

    @Test(expected = NotValidTransactionException.class)
    public void parse_String_NotValidTransactionException() throws Exception {
        parser.parse(transactionJson);
    }

    @Test
    public void getTxId() throws Exception {
        ErisCallTransaction transaction;
        transaction = parser.parse(tx3424287);
        assertNotNull(transaction);
        assertEquals(tx3424287Id, transaction.getTxId());
    }

    @Test
    public void getTxId_TwoTransactionInBlock() throws Exception {
        ErisCallTransaction transaction;

//        transaction = parser.parse(tx3772584FirstTransaction);
//        assertNotNull(transaction);
//        assertEquals(tx3772584FirstTransactionId, transaction.getTxId());
//
//        transaction = parser.parse(tx3772584SecondTransaction);
//        assertNotNull(transaction);
//        assertEquals(tx3772584SecondTransactionId, transaction.getTxId());
    }

    @Before
    public void setUp() throws Exception {
        parser = new Eris11CallTransactionParser();
        ObjectMapper objectMapper = new ObjectMapper();

        String path = "json/v11/transaction62.json";
        transactionJson = getStringFromFile(path);
        transactionArrayNode = objectMapper.readTree(transactionJson);
        transactionArrayList = objectMapper.readValue(new File(root + path), Object.class);

        txWithoutPubKey = objectMapper.readValue(new File(root + "json/v11/txWithoutPubKey.json"), Object.class);

        tx3424287 = getUndefinedTxFromBlock("/json/v11/block3424287.json",0);
        tx3772584FirstTransaction = getUndefinedTxFromBlock("/json/v11/block3772584.json",1);
        tx3772584SecondTransaction = getUndefinedTxFromBlock("/json/v11/block3772584.json",2);

        tx3424287Id = "21AC0F3819FBA0B05F54B715F84B9E76B97AFD09";
        tx3772584FirstTransactionId = "9a4bd15449ad77f7e69def67177489c1c1fe49d9";
        tx3772584SecondTransactionId = "6aba851efc88df65d3c4b5a2616d4b3c800f0dec";

    }

    private static ErisUndefinedTransaction getUndefinedTxFromBlock(String path,int index) throws FileNotFoundException {
        String json = getStringFromFile(path);
        ErisBlock block = new ErisRPCResponseEntity<>(json, ErisBlock.class).getResult();
        return new ErisUndefinedTransaction(block.getUndefinedTransactions().get(index), block.getHeader());
    }
}