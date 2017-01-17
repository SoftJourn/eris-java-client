package com.softjourn.eris.transaction;


import com.softjourn.eris.transaction.type.Block;
import org.junit.Test;

import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * TransactionHelperTest
 * Created by vromanchuk on 12.01.17.
 */
public class TransactionHelperTest {

    String chainUrl = "http://172.17.0.1:1337";

    private TransactionHelper transactionHelper = new TransactionHelper(chainUrl);
    private BigInteger blockNumber = new BigInteger("10");

    @Test
    public void getBlockJSON() throws Exception {
        String blockJSON = transactionHelper.getBlockJSON(blockNumber);
        System.out.println(blockJSON);
        assertNotNull(blockJSON);
        assertFalse(blockJSON.isEmpty());
    }

    @Test
    public void getBlock_BlockN10_BlockN10() throws Exception {
        assertThat(transactionHelper.getBlock(blockNumber), instanceOf(Block.class));
        Block block = transactionHelper.getBlock(blockNumber);
        assertNotNull(block);
        assertNotNull(block.getHeader());
        assertNotNull(block.getLastCommit());
        assertNotNull(block.getData());
        assertEquals(blockNumber, block.getHeader().getHeight());
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
        BigInteger heightLast = transactionHelper.getLatestBlockNumber();
        assertNotEquals(heightFirst, heightLast);
    }

    @Test
    public void getLatestBlockNumber_BigInteger() throws Exception {
        assertThat(transactionHelper.getLatestBlockNumber(), instanceOf(BigInteger.class));
        assertNotNull(transactionHelper.getLatestBlockNumber());
    }

    @Test
    public void getBlock_RandBlockNLessThanLatest_Block() throws Exception {
        BigInteger latest = transactionHelper.getLatestBlockNumber();
        double doubleRandom = latest.doubleValue() * Math.random();
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
