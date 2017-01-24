package com.softjourn.eris.transaction;


import com.softjourn.eris.contract.ContractUnit;
import com.softjourn.eris.filter.FilterData;
import com.softjourn.eris.filter.Filters;
import com.softjourn.eris.filter.Operation;
import com.softjourn.eris.filter.type.FilterHeight;
import com.softjourn.eris.rpc.ErisRPCRequestEntity;
import com.softjourn.eris.rpc.HTTPRPCClient;
import com.softjourn.eris.rpc.RPCMethod;
import com.softjourn.eris.transaction.type.Block;
import com.softjourn.eris.transaction.type.Blocks;
import com.softjourn.eris.transaction.type.ErisTransaction;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TransactionHelperTest
 * Created by vromanchuk on 12.01.17.
 */
@RunWith(JUnit4.class)
public class TransactionHelperTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private String chainUrl = "http://172.17.0.1:1337";
    private TransactionHelper transactionHelper = new TransactionHelper(chainUrl);
    private BigInteger blockNumberTen = BigInteger.TEN;
    private BigInteger blockNumberWithTx3847 = new BigInteger("3847");
    private BigInteger blockNumber3846 = new BigInteger("3846");
    private BigInteger blockNumber3848 = new BigInteger("3848");
    private HTTPRPCClient httpRpcClient = mock(HTTPRPCClient.class);
    private double random = Math.random();
    private boolean isRealCallsToEris = true;
    private String abi;

    @Before
    public void setUp() throws Exception {
        File abiFile = new File("src/test/resources/json/coinsContractAbi.json");
        this.abi = new Scanner(abiFile).useDelimiter("\\Z").next();

        if (!isRealCallsToEris) {
            Field field = transactionHelper.getClass().getDeclaredField("httpRpcClient");
            field.setAccessible(true);
            field.set(transactionHelper, httpRpcClient);

            Map<String, Object> param;
            ErisRPCRequestEntity entity;
            File file;
            //Block 10
            param = new HashMap<>();
            param.put("height", blockNumberTen);
            entity = new ErisRPCRequestEntity(param, RPCMethod.GET_BLOCK);
            file = new File("src/test/resources/json/block10.json");
            when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next());

            //Block 3847
            param = new HashMap<>();
            param.put("height", blockNumberWithTx3847);
            entity = new ErisRPCRequestEntity(param, RPCMethod.GET_BLOCK);
            file = new File("src/test/resources/json/block3847.json");
            when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next());

            //Get latest block
            entity = new ErisRPCRequestEntity(null, RPCMethod.GET_LATEST_BLOCK);
            file = new File("src/test/resources/json/height3847.json");
            when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next());

            //Random
            this.random = 1;

            //Greater block
            BigInteger latest = transactionHelper.getLatestBlockNumber();
            BigInteger greater = latest.add(BigInteger.TEN);
            param = new HashMap<>();
            param.put("height", greater);
            entity = new ErisRPCRequestEntity(param, RPCMethod.GET_BLOCK);
            file = new File("src/test/resources/json/emptyResponse.json");
            when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next());

            Filters filters;
            FilterData filterFrom;
            FilterData filterTo;

            //Blocks range 3846-3848
            filters = new Filters();
            filterFrom = new FilterHeight(Operation.GREATER_OR_EQUALS, blockNumber3846);
            filterTo = new FilterHeight(Operation.LESS_OR_EQUALS, blockNumber3848);
            filters.add(filterFrom);
            filters.add(filterTo);
            entity = new ErisRPCRequestEntity(filters.getMap(), RPCMethod.GET_BLOCKS);
            file = new File("src/test/resources/json/blockRange3846-3848.json");
            when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next().replaceAll("\\n ", ""));

            //Blocks range 0-10
            filters = new Filters();
            filterFrom = new FilterHeight(Operation.GREATER_OR_EQUALS, BigInteger.ZERO);
            filterTo = new FilterHeight(Operation.LESS_OR_EQUALS, BigInteger.TEN);
            filters.add(filterFrom);
            filters.add(filterTo);
            entity = new ErisRPCRequestEntity(filters.getMap(), RPCMethod.GET_BLOCKS);
            file = new File("src/test/resources/json/blockRange0-10.json");
            when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next().replaceAll("\\n ", ""));

            //Blocks range 1-51
            filters = new Filters();
            filterFrom = new FilterHeight(Operation.GREATER_OR_EQUALS, BigInteger.ONE);
            filterTo = new FilterHeight(Operation.LESS_OR_EQUALS, BigInteger.valueOf(51));
            filters.add(filterFrom);
            filters.add(filterTo);
            entity = new ErisRPCRequestEntity(filters.getMap(), RPCMethod.GET_BLOCKS);
            file = new File("src/test/resources/json/blockRange1-51.json");
            when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next().replaceAll("\\n ", ""));

            //Blocks range 51-75
            filters = new Filters();
            filterFrom = new FilterHeight(Operation.GREATER_OR_EQUALS, BigInteger.valueOf(52));
            filterTo = new FilterHeight(Operation.LESS_OR_EQUALS, BigInteger.valueOf(75));
            filters.add(filterFrom);
            filters.add(filterTo);
            entity = new ErisRPCRequestEntity(filters.getMap(), RPCMethod.GET_BLOCKS);
            file = new File("src/test/resources/json/blockRange52-75.json");
            when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next().replaceAll("\\n ", ""));

        }
    }

    @Test
    public void getBlockJSON() throws Exception {
        String blockJSON = transactionHelper.getBlockJSON(BigInteger.valueOf(15));
        System.out.println(blockJSON);
        assertNotNull(blockJSON);
        assertFalse(blockJSON.isEmpty());
    }

    @Test
    public void getBlock_BlockN10_BlockN10() throws Exception {
        assertThat(transactionHelper.getBlock(blockNumberTen), instanceOf(Block.class));
        Block block = transactionHelper.getBlock(blockNumberTen);
        assertNotNull(block);
        assertNotNull(block.getHeader());
        assertNotNull(block.getLastCommit());
        assertNotNull(block.getData());
        assertEquals(blockNumberTen, block.getHeader().getHeight());
    }

    @Test
    public void getBlock_BlockN3847_BlockN3847() throws Exception {
        BigInteger blockN3847 = new BigInteger("3847");
        assertThat(transactionHelper.getBlock(blockN3847), instanceOf(Block.class));
        Block block = transactionHelper.getBlock(blockN3847);
        assertEquals(blockN3847, block.getHeader().getHeight());
    }


    @Test
    public void getLatestBlock_After1sDifferentHeight() throws Exception {
        BigInteger heightFirst = transactionHelper.getLatestBlockNumber();
        Thread.sleep(1000L);
        if (!isRealCallsToEris) {
            File file = new File("src/test/resources/json/height10.json");
            when(httpRpcClient.call(any())).thenReturn(new Scanner(file).useDelimiter("\\Z").next());
        }
        BigInteger heightLast = transactionHelper.getLatestBlockNumber();
        assertNotEquals(heightFirst, heightLast);
    }

    @Test
    public void getLatestBlockNumber_BigInteger() throws Exception {
        BigInteger latestBlockNumber = transactionHelper.getLatestBlockNumber();
        assertThat(latestBlockNumber, instanceOf(BigInteger.class));
        assertNotNull(latestBlockNumber);

    }

    @Test
    public void getBlock_RandBlockNLessThanLatest_Block() throws Exception {
        BigInteger latest = transactionHelper.getLatestBlockNumber();
        double doubleRandom = latest.doubleValue() * this.random;
        Integer integerRandom = (int) doubleRandom;
        BigInteger rand = new BigInteger(integerRandom.toString());
        assertNotNull(transactionHelper.getBlock(rand));
        assertNotNull(transactionHelper.getBlock(rand).getHeader());
        assertNotNull(transactionHelper.getBlock(rand).getHeader().getHeight());
        assertEquals(rand, transactionHelper.getBlock(rand).getHeader().getHeight());

    }

    @Test
    public void getBlock_BlockGreaterThanLatest_Null() throws Exception {
        BigInteger latest = transactionHelper.getLatestBlockNumber();
        BigInteger greater = latest.add(BigInteger.TEN);
        assertNull(transactionHelper.getBlock(greater));
    }

    @Test
    public void getTransactionFromBlock() throws Exception {
        if (!isRealCallsToEris) {
            String expected = "[90CCB0132FA9287AB3C3283978C0E523FA1450A0, 110]";
            List<ErisTransaction> erisTransactions = transactionHelper.getBlock(blockNumberWithTx3847).getData().getErisTransactions();
            assertEquals(1, erisTransactions.size());
            ErisTransaction erisTransaction = erisTransactions.get(0);
            ContractUnit unit = erisTransaction.getContractUnit(abi);
            Map<String, String> inputs = erisTransaction.parseCallingData(unit);
            assertEquals(expected, inputs.toString());
        }
    }

    @Test
    public void getTransactionBlock_3846_3848() throws Exception {
        Blocks blocks = transactionHelper.getBlocks(blockNumber3846, blockNumber3848);
        assertEquals(3, blocks.getBlockMetas().size());
        List<BigInteger> blockNumbersWithTx = blocks.getBlockNumbersWithTransaction();
        assertEquals(1, blockNumbersWithTx.size());
        assertEquals(blockNumberWithTx3847, blockNumbersWithTx.get(0));
    }

    @Test
    public void getTransactionBlock_0_10_IllegalArgumentException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("From height can't be less then 1");
        transactionHelper.getBlocks(BigInteger.ZERO, BigInteger.TEN);
    }

    @Test
    public void getTransactionBlock_10_0_IllegalArgumentException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("From height can't be grater then To height");
        transactionHelper.getBlocks(BigInteger.TEN, BigInteger.ZERO);
    }

    @Test
    public void getTransactionBlock_1_75_75Elements() throws Exception {
        Blocks blocks = transactionHelper.getBlocks(BigInteger.ONE, BigInteger.valueOf(75));
        assertEquals(75, blocks.getBlockMetas().size());
        List<BigInteger> transactionBlocks = blocks.getBlockNumbersWithTransaction();
        System.out.println(transactionBlocks);
        assertEquals(3, transactionBlocks.size());
    }

}
