package com.softjourn.eris.transaction.type;

import com.softjourn.eris.contract.ContractUnit;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * TransactionTest
 * Created by vromanchuk on 13.01.17.
 */
public class ErisTransactionTest {

    private String transactionBinary;
    private ErisTransaction transaction;
    private String abi;
    private String wrongAbi;
    private String largeSequenceTransaction;
    private String deploy;

    @Test
    public void isDeployContractTx() throws Exception {
        assertTrue(ErisTransaction.isDeployContractTx(deploy));
        assertFalse(ErisTransaction.isDeployContractTx(transactionBinary));

        ErisTransaction transaction;

        transaction = new ErisTransaction(deploy);
        assertTrue(transaction.getIsDeploy());
        assertEquals(transaction.getCallingData(), deploy);

        transaction = new ErisTransaction(transactionBinary);
        assertFalse(transaction.getIsDeploy());

    }

    @Test
    public void newTransaction_NotNullTransaction() throws Exception {
        assertNotNull(transaction);
        System.out.println(transaction);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newTransaction_fakeString_StringIndexOutOfBoundsException() throws Exception {
        new ErisTransaction("fake transaction");
    }

    @Test
    public void getContractUnit_abi_unit() throws Exception {
        ContractUnit unit = transaction.getContractUnit(abi);
        assertNotNull(unit);
    }

    @Test
    public void parseCallingData_ContractUnit_Map() throws Exception {
        ContractUnit unit = transaction.getContractUnit(abi);
        Map<String, String> parseData = transaction.parseCallingData(unit);
        assertNotNull(parseData);
        String expected = "[90CCB0132FA9287AB3C3283978C0E523FA1450A0, 110]";
        assertEquals(expected, parseData.values().toString());
        System.out.println(parseData);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseCallingData_WrongAbi_IllegalArgumentException() throws Exception {
        ContractUnit unit = transaction.getContractUnit(wrongAbi);
        transaction.parseCallingData(unit);
    }

    @Test
    public void generateTxCode() throws Exception {
        ErisTransaction erisTransaction;

        erisTransaction = new ErisTransaction(transactionBinary);
        assertEquals(transactionBinary, erisTransaction.generateTxCode());
        System.out.println(erisTransaction.getContractAddress());

        erisTransaction = new ErisTransaction(largeSequenceTransaction);
        assertEquals(largeSequenceTransaction, erisTransaction.generateTxCode());

    }

    @Before
    public void setUp() throws Exception {
        File file;

        file = new File("src/test/resources/json/v12/coinsContractAbi.json");
        this.abi = new Scanner(file).useDelimiter("\\Z").next();

        file = new File("src/test/resources/json/v12/wrong_abi.json");
        this.wrongAbi = new Scanner(file).useDelimiter("\\Z").next();

        file = new File("src/test/resources/binary/TransactionBinary.txt");
        this.transactionBinary = new Scanner(file).useDelimiter("\\Z").next();

        file = new File("src/test/resources/binary/LargeSequenceTransaction.txt");
        this.largeSequenceTransaction = new Scanner(file).useDelimiter("\\Z").next();

        file = new File("src/test/resources/binary/DeployTransaction.txt");
        this.deploy = new Scanner(file).useDelimiter("\\Z").next();

        this.transaction = new ErisTransaction(transactionBinary);
    }
}
