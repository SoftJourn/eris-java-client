package com.softjourn.eris.transaction.parser.v12;

import com.softjourn.eris.transaction.pojo.ErisCallTransaction;
import com.softjourn.eris.transaction.pojo.ErisTransactionType;
import com.softjourn.eris.transaction.pojo.NotValidTransactionException;
import org.junit.Before;
import org.junit.Test;

import static com.softjourn.eris.TestUtil.getStringFromFile;
import static org.junit.Assert.*;

public class Eris12CallTransactionParserTest {

    private Eris12CallTransactionParser parser;
    private ErisCallTransaction transaction;
    private String transactionBinary;
    private String transactionDeployBinary;
    private String deployCallingData;
    private String largeSequenceTransaction;

    @Test
    public void getTransactionType() throws Exception {
        assertEquals(ErisTransactionType.CALL,parser.getTransactionType());
    }

    @Test
    public void parse() throws Exception {

        transaction = parser.parse(transactionBinary);
        assertNotNull(transaction);
        assertNotNull(transaction.getAmount());

    }

    @Test
    public void generateOrigin() throws Exception {
        transaction = parser.parse(transactionBinary);
        assertEquals(transactionBinary,parser.generateOrigin(transaction));
    }

    @Test
    public void parse_DeployTxBinary_ErisCallTransaction() throws Exception {

        transaction = parser.parse(transactionDeployBinary);
        assertTrue(transaction.getIsDeploy());
        assertEquals(transaction.getCallingData(), deployCallingData);

    }

    @Test
    public void generateOrigin_DeployErisCallTransaction_DeployTxBinary() throws Exception {
        transaction = parser.parse(transactionDeployBinary);
        assertEquals(transactionDeployBinary,parser.generateOrigin(transaction));
    }

    @Test
    public void generateOrigin_LargeSequenceTransaction_Binary() throws Exception {
        transaction = parser.parse(largeSequenceTransaction);
        assertEquals(largeSequenceTransaction, parser.generateOrigin(transaction));
    }

    @Test(expected = NotValidTransactionException.class)
    public void parser_fakeString_NotValidTransactionException() throws Exception {
        parser.parse("fake transaction");
    }


    @Before
    public void setUp() throws Exception {
        parser = new Eris12CallTransactionParser();

        transactionBinary = getStringFromFile("binary/TransactionBinary.txt");
        transactionDeployBinary = getStringFromFile("binary/TransactionDeployBinary.txt");
        deployCallingData = getStringFromFile("binary/TransactionDeployBinaryCallingData.txt");
        largeSequenceTransaction = getStringFromFile("binary/LargeSequenceTransaction.txt");

    }
}