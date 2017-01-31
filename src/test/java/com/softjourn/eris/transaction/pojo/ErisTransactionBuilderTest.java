package com.softjourn.eris.transaction.pojo;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Scanner;

public class ErisTransactionBuilderTest {
    private String transactionBinary;
    private ErisTransactionBuilder erisTransactionBuilder;

    @Test
    public void parse() throws Exception {
        ErisTransactionBuilder.parse()
    }

    @Before
    public void setUp() throws Exception {
        erisTransactionBuilder = new ErisTransactionBuilder();

        File file;
        file = new File("src/test/resources/binary/TransactionBinary.txt");
        this.transactionBinary = new Scanner(file).useDelimiter("\\Z").next();

    }
}