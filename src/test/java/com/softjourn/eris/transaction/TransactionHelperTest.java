package com.softjourn.eris.transaction;


import com.softjourn.eris.contract.ContractUnit;
import com.softjourn.eris.filter.FilterData;
import com.softjourn.eris.filter.Filters;
import com.softjourn.eris.filter.Operation;
import com.softjourn.eris.filter.type.FilterHeight;
import com.softjourn.eris.rpc.ErisRPCRequestEntity;
import com.softjourn.eris.rpc.HTTPRPCClient;
import com.softjourn.eris.rpc.RPCMethod;
import com.softjourn.eris.transaction.pojo.Block;
import com.softjourn.eris.transaction.pojo.BlockMeta;
import com.softjourn.eris.transaction.pojo.Blocks;
import com.softjourn.eris.transaction.pojo.ErisTransaction;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.lang.reflect.Field;
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
    private Long blockNumberTen = 10L;
    private Long blockNumberWithTx3847 = new Long("3847");
    private Long blockNumber3846 = new Long("3846");
    private Long blockNumber3848 = new Long("3848");
    private HTTPRPCClient httpRpcClient = mock(HTTPRPCClient.class);
    private double random = Math.random();
    private boolean isRealCallsToEris = false;
    private String abi;
    private Long blockSixtyTwo = 62L;

    @Test
    public void getBlockJSON() throws Exception {
        String blockJSON;

        blockJSON = transactionHelper.getBlockJSON(blockNumberWithTx3847);
        System.out.println(blockJSON);
        assertNotNull(blockJSON);
        assertFalse(blockJSON.isEmpty());

        blockJSON = transactionHelper.getBlockJSON(blockSixtyTwo);
        assertNotNull(blockJSON);
        System.out.println(blockJSON);
    }

    @Test
    public void getBlock_BlockN62_BlockN62() throws Exception {
        Block block = transactionHelper.getBlock(blockSixtyTwo);

        assertNotNull(block);
        assertNotNull(block.getHeader());
        assertNotNull(block.getData());

        assertEquals(blockSixtyTwo, block.getHeader().getHeight());
        //Checking parsed transaction
        assertEquals(1,block.getData().getErisTransactions().size());
        ErisTransaction transaction = block.getData().getErisTransactions().get(0);
        transaction.getFeeLongValue();
//        assertEquals(1234L, transaction.getFeeLongValue());
    }

    @Test
    public void getBlock_BlockN10_BlockN10() throws Exception {
        assertThat(transactionHelper.getBlock(blockNumberTen), instanceOf(Block.class));
        Block block = transactionHelper.getBlock(blockNumberTen);
        assertNotNull(block);
        assertNotNull(block.getHeader());
        assertNotNull(block.getData());
        assertEquals(blockNumberTen, block.getHeader().getHeight());
    }

    @Test
    public void getBlock_BlockN3847_BlockN3847() throws Exception {
        Long blockN3847 = new Long("3847");
        assertThat(transactionHelper.getBlock(blockN3847), instanceOf(Block.class));
        Block block = transactionHelper.getBlock(blockN3847);
        assertEquals(blockN3847, block.getHeader().getHeight());
    }


    @Test
    public void getLatestBlock_After1sDifferentHeight() throws Exception {
        Long heightFirst = transactionHelper.getLatestBlockNumber();
        Thread.sleep(1000L);
        if (!isRealCallsToEris) {
            File file = new File("src/test/resources/json/v12/height10.json");
            when(httpRpcClient.call(any())).thenReturn(new Scanner(file).useDelimiter("\\Z").next());
        }
        Long heightLast = transactionHelper.getLatestBlockNumber();
        assertNotEquals(heightFirst, heightLast);
    }

    @Test
    public void getLatestBlockNumber_Long() throws Exception {
        Long latestBlockNumber = transactionHelper.getLatestBlockNumber();
        assertThat(latestBlockNumber, instanceOf(Long.class));
        assertNotNull(latestBlockNumber);

    }

    @Test
    public void getBlock_RandBlockNLessThanLatest_Block() throws Exception {
        Long latest = transactionHelper.getLatestBlockNumber();
        double doubleRandom = latest.doubleValue() * this.random;
        Integer integerRandom = (int) doubleRandom;
        Long rand = new Long(integerRandom.toString());
        assertNotNull(transactionHelper.getBlock(rand));
        assertNotNull(transactionHelper.getBlock(rand).getHeader());
        assertNotNull(transactionHelper.getBlock(rand).getHeader().getHeight());
        assertEquals(rand, transactionHelper.getBlock(rand).getHeader().getHeight());

    }

    @Test
    public void getBlock_BlockGreaterThanLatest_Null() throws Exception {
        Long latest = transactionHelper.getLatestBlockNumber();
        Long greater = latest + 10L;
        assertNull(transactionHelper.getBlock(greater));
    }

    @Test
    public void getTransactionFromBlock() throws Exception {
        if (!isRealCallsToEris) {
            String expected = "{owner=90CCB0132FA9287AB3C3283978C0E523FA1450A0, amount=110}";
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
        List<BlockMeta> blocks = transactionHelper.getBlocks(blockNumber3846, blockNumber3848);
        assertEquals(2, blocks.size());
        List<Long> blockNumbersWithTx = Blocks.getBlockNumbersWithTransaction(blocks);
        assertEquals(1, blockNumbersWithTx.size());
        assertEquals(blockNumberWithTx3847, blockNumbersWithTx.get(0));
    }

    @Test
    public void getTransactionBlock_0_10_IllegalArgumentException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("From height can't be less then 1");
        transactionHelper.getBlocks(0L, 10L);
    }

    @Test
    public void getTransactionBlock_10_0_IllegalArgumentException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("From height can't be grater or equals To height");
        transactionHelper.getBlocks(10L, 0L);
    }

    @Test
    public void getTransactionBlock_1_75_75Elements() throws Exception {
        List<BlockMeta> blocks = transactionHelper.getBlocks(1L, 76L);
        assertEquals(75, blocks.size());
        List<Long> transactionBlocks = Blocks.getBlockNumbersWithTransaction(blocks);
        System.out.println(transactionBlocks);
        assertEquals(3, transactionBlocks.size());
    }

    @Test
    public void getBlocks() throws Exception {
        Long to = 53L;
        System.out.println("block to param = " + to);
        System.out.println(transactionHelper.getBlocks(1L, to).size());
    }

    @Before
    public void setUp() throws Exception {
        File abiFile = new File("src/test/resources/json/v12/coinsContractAbi.json");
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
            file = new File("src/test/resources/json/v12/block10.json");
            when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next());

            //Block 62 v11
            param = new HashMap<>();
            param.put("height", blockSixtyTwo);
            entity = new ErisRPCRequestEntity(param, RPCMethod.GET_BLOCK);
            file = new File("src/test/resources/json/v11/block62.json");
            when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next());

            //Block 3847
            param = new HashMap<>();
            param.put("height", blockNumberWithTx3847);
            entity = new ErisRPCRequestEntity(param, RPCMethod.GET_BLOCK);
            file = new File("src/test/resources/json/v12/block3847.json");
            when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next());

            //Get latest block
            entity = new ErisRPCRequestEntity(null, RPCMethod.GET_LATEST_BLOCK);
            file = new File("src/test/resources/json/v12/height3847.json");
            when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next());

            //Random
            this.random = 1;

            //Greater block
            Long latest = transactionHelper.getLatestBlockNumber();
            Long greater = latest + 10;
            param = new HashMap<>();
            param.put("height", greater);
            entity = new ErisRPCRequestEntity(param, RPCMethod.GET_BLOCK);
            file = new File("src/test/resources/json/v12/emptyResponse.json");
            when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next());

            Filters filters;
            FilterData filterFrom;
            FilterData filterTo;

            //Blocks range 3846-3848
            filters = new Filters();
            filterFrom = new FilterHeight(Operation.GREATER_OR_EQUALS, blockNumber3846);
            filterTo = new FilterHeight(Operation.LESS, blockNumber3848);
            filters.add(filterFrom);
            filters.add(filterTo);
            entity = new ErisRPCRequestEntity(filters.getMap(), RPCMethod.GET_BLOCKS);
            file = new File("src/test/resources/json/v12/blockRange3846-3847.json");
            when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next().replaceAll("\\n ", ""));

            //Blocks range 0-10
            filters = new Filters();
            filterFrom = new FilterHeight(Operation.GREATER_OR_EQUALS, 0L);
            filterTo = new FilterHeight(Operation.LESS_OR_EQUALS, 10L);
            filters.add(filterFrom);
            filters.add(filterTo);
            entity = new ErisRPCRequestEntity(filters.getMap(), RPCMethod.GET_BLOCKS);
            file = new File("src/test/resources/json/v12/blockRange0-10.json");
            when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next().replaceAll("\\n ", ""));

            //Blocks range 1-53
            filters = new Filters();
            filterFrom = new FilterHeight(Operation.GREATER_OR_EQUALS, 1L);
            filterTo = new FilterHeight(Operation.LESS, 53L);
            filters.add(filterFrom);
            filters.add(filterTo);
            entity = new ErisRPCRequestEntity(filters.getMap(), RPCMethod.GET_BLOCKS);
            file = new File("src/test/resources/json/v12/blockRange1-52.json");
            when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next().replaceAll("\\n ", ""));

            //Blocks range 52-75
            filters = new Filters();
            filterFrom = new FilterHeight(Operation.GREATER_OR_EQUALS, 52L);
            filterTo = new FilterHeight(Operation.LESS, 76L);
            filters.add(filterFrom);
            filters.add(filterTo);
            entity = new ErisRPCRequestEntity(filters.getMap(), RPCMethod.GET_BLOCKS);
            file = new File("src/test/resources/json/v12/blockRange53-75.json");
            when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next().replaceAll("\\n ", ""));

        }
    }

}
