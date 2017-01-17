package com.softjourn.eris.transaction.type;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

import static org.junit.Assert.assertNotNull;

/**
 * TransactionTest
 * Created by vromanchuk on 13.01.17.
 */
public class TransactionTest {

    private String transactionBinary;
    private String abi;

    @Before
    public void setUp() throws Exception {
        File file;
        file = new File("src/test/resources/json/coinsContractAbi.json");
        this.abi = new Scanner(file).useDelimiter("\\Z").next();
        file = new File("src/test/resources/TransactionBinary.txt");
        this.transactionBinary = new Scanner(file).useDelimiter("\\Z").next();

    }

    @Test
    public void newTransaction_NotNullTransaction() throws Exception {
        assertNotNull(new Transaction(transactionBinary));
        System.out.println(new Transaction(transactionBinary));
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void newTransaction_fakeString_StringIndexOutOfBoundsException() throws Exception {
        new Transaction("fake transaction");
    }

    @Test
    public void parseCallingData() throws Exception {
        Transaction transaction = new Transaction(transactionBinary);
        assertNotNull(transaction.parseCallingData(abi));
        transaction.parseCallingData(abi);
        System.out.println(transaction.parseCallingData(abi));

    }
}
