package com.softjourn.eris.transaction;


import com.softjourn.eris.rpc.ErisRPCRequestEntity;
import com.softjourn.eris.rpc.HTTPRPCClient;
import com.softjourn.eris.rpc.RPCMethod;
import com.softjourn.eris.transaction.type.Block;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.HashMap;
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

    private String chainUrl = "http://172.17.0.1:1337";

    private TransactionHelper transactionHelper = new TransactionHelper(chainUrl);
    private BigInteger blockNumberTen = BigInteger.TEN;
    private BigInteger blockNumber3847 = new BigInteger("3847");

    private HTTPRPCClient httpRpcClient = mock(HTTPRPCClient.class);
    private double random = Math.random();
    private boolean isRealCallsToEris = false;

    @Before
    public void setUp() throws Exception {
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
            param.put("height", blockNumber3847);
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

        }
    }

    @Test
    public void getBlockJSON() throws Exception {
        String blockJSON = transactionHelper.getBlockJSON(blockNumber3847);
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
        System.out.println(block);
        assertEquals(blockNumberTen, block.getHeader().getHeight());
    }

    @Test
    public void getBlock_BlockN3847_BlockN3847() throws Exception {
        BigInteger blockN3847 = new BigInteger("3847");
        assertThat(transactionHelper.getBlock(blockN3847), instanceOf(Block.class));
        Block block = transactionHelper.getBlock(blockN3847);
        System.out.println(block.getData());
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
        System.out.println(latestBlockNumber);

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
        System.out.println(latest);
        BigInteger greater = latest.add(BigInteger.TEN);
        assertNull(transactionHelper.getBlock(greater));
    }
}
