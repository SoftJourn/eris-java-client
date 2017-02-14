package com.softjourn.eris.transaction.parser;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static com.softjourn.eris.TestUtil.getStringFromFile;
import static org.junit.Assert.assertEquals;

public class ErisCallDataTransactionParserTest {

    private String callingData;
    private String abi;
    private String parsedCallingData;
    private String wrongAbi;

    @Test
    public void parse() throws Exception {
        Map data = ErisCallDataTransactionParser.parseCallingData(callingData,abi);
        assertEquals(parsedCallingData,data.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parse_WrongAbi_IllegalArgumentException() throws Exception {
        ErisCallDataTransactionParser.parseCallingData(callingData,wrongAbi);
    }

    @Before
    public void setUp() throws Exception {
        callingData = getStringFromFile("binary/TransactionBinaryCallingData.txt");
        parsedCallingData = "{owner=90CCB0132FA9287AB3C3283978C0E523FA1450A0, amount=110}";
        abi = getStringFromFile("json/coinsContractAbi.json");
        wrongAbi = getStringFromFile("json/wrong_abi.json");
    }

}