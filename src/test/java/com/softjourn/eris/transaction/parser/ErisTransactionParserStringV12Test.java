package com.softjourn.eris.transaction.parser;

import com.softjourn.eris.transaction.pojo.ErisTransaction;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ErisTransactionParserStringV12Test {

    private IErisTransactionParser parser;
    private String transactionBinary;
    private String transactionDeployBinary;


    @Test
    public void parse() throws Exception {
        ErisTransaction transaction = parser.parse(transactionBinary);
        assertNotNull(transaction);
        assertNotNull(transaction.getAmount());
        assertEquals(transactionBinary,transaction.generateTxCode());
    }

    @Test
    public void parse_DeployTransaction() throws Exception {
        ErisTransaction transaction = parser.parse(transactionDeployBinary);
        assertNotNull(transaction);
        assertNotNull(transaction.getAmount());

    }

    @Before
    public void setUp() throws Exception {
        parser = new ErisTransactionParserStringV12();

        File file;
        file = new File("src/test/resources/binary/TransactionBinary.txt");
        transactionBinary = new Scanner(file).useDelimiter("\\Z").next();

        file = new File("src/test/resources/binary/TransactionDeployBinary.txt");
        transactionDeployBinary = new Scanner(file).useDelimiter("\\Z").next();
    }
}