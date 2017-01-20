package com.softjourn.eris.transaction.type;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * TransactionTest
 * Created by vromanchuk on 13.01.17.
 */
public class ErisTransactionTest {

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
        assertNotNull(new ErisTransaction(transactionBinary));
        System.out.println(new ErisTransaction(transactionBinary));
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void newTransaction_fakeString_StringIndexOutOfBoundsException() throws Exception {
        new ErisTransaction("fake transaction");
    }

    @Test
    public void parseCallingData_Abi() throws Exception {
        ErisTransaction erisTransaction = new ErisTransaction(transactionBinary);
        assertNotNull(erisTransaction.parseCallingData(abi));
        List<Object> parseData = erisTransaction.parseCallingData(abi);
        String expected = "[90CCB0132FA9287AB3C3283978C0E523FA1450A0, 110]";
        assertEquals(expected, parseData.toString());
    }

    @Test
    public void generateTxCode() throws Exception {
        ErisTransaction erisTransaction = new ErisTransaction(transactionBinary);
        assertEquals(transactionBinary, erisTransaction.generateTxCode());
    }
}
