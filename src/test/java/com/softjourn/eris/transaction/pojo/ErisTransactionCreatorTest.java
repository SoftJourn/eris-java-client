package com.softjourn.eris.transaction.pojo;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

public class ErisTransactionCreatorTest {
    private String transactionBinary;
    private ErisTransactionCreator erisTransactionCreator;

    @Test
    public void parse() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        erisTransactionCreator = new ErisTransactionCreator();

        File file;
        file = new File("src/test/resources/binary/TransactionBinary.txt");
        this.transactionBinary = new Scanner(file).useDelimiter("\\Z").next();



    }
}