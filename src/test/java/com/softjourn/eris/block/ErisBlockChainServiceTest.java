package com.softjourn.eris.block;

import com.softjourn.eris.block.pojo.Block;
import com.softjourn.eris.filter.Filters;
import com.softjourn.eris.filter.Operation;
import com.softjourn.eris.filter.type.FilterHeight;
import com.softjourn.eris.rpc.ErisRPCError;
import com.softjourn.eris.rpc.ErisRPCRequestEntity;
import com.softjourn.eris.rpc.HTTPRPCClient;
import com.softjourn.eris.rpc.RPCMethod;
import com.softjourn.eris.transaction.pojo.ClassifiableErisTransaction;
import lombok.extern.java.Log;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This test works with mocks from eris blockchain version 11 and 12
 * <p>It can be used for debug purpose and can be easily switched calls to real eris</p>
 * Local http://172.17.0.1:1337
 * Remote testing url http://46.101.203.71:1337
 */

@Log
public class ErisBlockChainServiceTest {
    // Disable info stack trace if run tests
    static {
        boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
                getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
        if (!isDebug)
            log.setLevel(Level.OFF);
    }

    private static boolean isRealCallsToEris = false;

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private String chainUrl = "http://46.101.203.71:1337";

    private ErisBlockChainService erisTransactionService = new ErisBlockChainService(chainUrl);

    private HTTPRPCClient httpRpcClient;

    private double random = Math.random();

    private String abi;

    private long block10 = 10;
    private long block62 = 62;
    private long block3846 = 3846;
    private long block3847WithTx = 3847;
    private long block3848 = 3848;

    @Test
    public void getBlockJson() throws Exception {
        String json;

        json = erisTransactionService.getBlockJson(block3847WithTx);
        log.info("block 3847 includes transaction");
        log.info(json);
        assertNotNull(json);
        assertFalse(json.isEmpty());

        json = erisTransactionService.getBlockJson(block62);
        log.info("block 62");
        log.info(json);
        assertNotNull(json);
    }

    @Test
    public void getBlock() throws Exception {
        Block block = erisTransactionService.getBlock(block62);

        assertNotNull(block);

        assertNotNull(block.getBlockNumber());
        assertNotNull(block.getChainName());
        assertNotNull(block.getTimeCreated());
        assertNotNull(block.getTransactions());

        log.info(block.toString());

    }

    @Test
    public void getBlock_10_BlockN10() throws Exception {
        assertThat(erisTransactionService.getBlock(block10), instanceOf(Block.class));
        Block block = erisTransactionService.getBlock(block10);

        assertNotNull(block);
        assertNotNull(block.getHeader());
        assertNotNull(block.getData());
        assertEquals(block10, block.getHeader().getHeight().longValue());
    }

    @Test
    public void getBlock_3847_BlockN3847() throws Exception {
        assertThat(erisTransactionService.getBlock(block3847WithTx), instanceOf(Block.class));
        Block block = erisTransactionService.getBlock(block3847WithTx);
        assertEquals(block3847WithTx, block.getBlockNumber().longValue());
    }

    @Test
    public void getLatestBlockNumber() throws Exception {
        Long heightFirst = erisTransactionService.getLatestBlockNumber();

        assertNotNull(heightFirst);
        assertThat(heightFirst, instanceOf(Long.class));
        assertNotNull(heightFirst);

        log.info("First request for get latest block number " + heightFirst);
        Thread.sleep(1000L);
        if (!isRealCallsToEris) {
            setUpLatestBlockResponse("v12/height3847.json");
        }
        Long heightLast = erisTransactionService.getLatestBlockNumber();
        assertNotNull(heightLast);
        assertThat(heightFirst, instanceOf(Long.class));

        log.info("Second request for get latest block number " + heightLast);

        assertNotEquals(heightFirst, heightLast);
    }

    @Test
    public void getBlock_RandBlockNLessThanLatest_Block() throws Exception {
        Long latest = erisTransactionService.getLatestBlockNumber();
        double doubleRandom = latest.doubleValue() * this.random;
        Integer integerRandom = (int) doubleRandom;
        Long rand = new Long(integerRandom.toString());
        assertNotNull(erisTransactionService.getBlock(rand));
        assertNotNull(erisTransactionService.getBlock(rand).getHeader());
        assertNotNull(erisTransactionService.getBlock(rand).getHeader().getHeight());
        assertEquals(rand, erisTransactionService.getBlock(rand).getHeader().getHeight());

    }

    /**
     * Tested for eris v11
     *
     * @throws ErisRPCError ErisRPCError(code=-32603, message=height must be less than the current blockchain height
     */
    @Test(expected = ErisRPCError.class)
    public void getBlock_BlockGreaterThanLatest_Null() throws Exception {
        Long latest = erisTransactionService.getLatestBlockNumber();
        Long greater = latest + 10;
        log.info(erisTransactionService.getBlock(greater).toString());
    }

    @Test
    public void getBlockNumbersWithTx() throws Exception {

        List<Long> blockNumbersWithTx = erisTransactionService
                .getBlockNumbersWithTx(block3846, block3848)
                .collect(Collectors.toList());
        assertEquals(1, blockNumbersWithTx.size());
        assertEquals(block3847WithTx, blockNumbersWithTx.get(0).longValue());
    }

    @Test
    public void getBlockNumbersWithTx_1_75_3Elements() throws Exception {
        List<Long> transactionBlocks = erisTransactionService
                .getBlockNumbersWithTx(1,76)
                .collect(Collectors.toList());
        log.info(transactionBlocks.toString());
        assertEquals(3, transactionBlocks.size());
    }

    @Test
    public void getBlocksWithTransactions() throws Exception {
        List<Block> blocks = erisTransactionService
                .getBlocksWithTransactions(block3846, block3848)
                .collect(Collectors.toList());
        assertEquals(1, blocks.size());
        Block block = blocks.get(0);
        assertEquals(block3847WithTx, block.getBlockNumber().longValue());
        assertEquals(1, block.getTransactions().size());
    }

    @Test
    public void getBlocksWithTransactions_0_10_IllegalArgumentException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("From height can't be less then 1");
        erisTransactionService.getBlocksWithTransactions(0L, 10L);
    }

    @Test
    public void getBlocksWithTransactions_10_0_IllegalArgumentException() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("From height can't be grater or equals To height");
        erisTransactionService.getBlocksWithTransactions(10, 0);
    }


    @Test
    public void getTransactionFromBlock() throws Exception {
        if (!isRealCallsToEris) {
            String expected = "{owner=90CCB0132FA9287AB3C3283978C0E523FA1450A0, amount=110}";
            List<ClassifiableErisTransaction> erisTransactions = erisTransactionService.getBlock(block3847WithTx).getTransactions();
            assertEquals(1, erisTransactions.size());
            ClassifiableErisTransaction erisTransaction = erisTransactions.get(0);
            //TODO implement transaction data parsing from unit
        }
    }


    @Before
    public void setUp() throws Exception {
        File abiFile = new File("src/test/resources/json/v12/coinsContractAbi.json");
        this.abi = new Scanner(abiFile).useDelimiter("\\Z").next();

        if (!isRealCallsToEris) {
            // Inject httpRpcClient
            Field field = erisTransactionService.getClass().getDeclaredField("httpRpcClient");
            field.setAccessible(true);
            httpRpcClient = mock(HTTPRPCClient.class);
            field.set(erisTransactionService, httpRpcClient);

            setUpBlockCall(block10, "v12/block10.json");
            setUpBlockCall(block62, "v11/block62.json");
            setUpBlockCall(block3847WithTx, "v12/block3847.json");

            setUpLatestBlockResponse("v12/height10.json");

            //PreDefined when mock up
            this.random = 1;

            //Illegal request for block height grater than latest
            Long latest = erisTransactionService.getLatestBlockNumber();
            Long greater = latest + 10;
            setUpBlockCall(greater, "v11/blockGraterThanLatest.json");

            setUpBlockRangeCall(block3846, block3848, "v12/blockRange3846-3847.json");
            setUpBlockRangeCall(0, 10, "v12/blockRange0-10.json");
            setUpBlockRangeCall(1, 53, "v12/blockRange1-52.json");
            setUpBlockRangeCall(52, 76, "v12/blockRange53-75.json");

        }

    }

    private final String rootPath = "src/test/resources/json/";

    /**
     * <p>Please specify eris version and file name</p>
     *
     * @param blockNumber height of block that you want to set up
     * @param relPath     relative path from json folder. Full path ../src/test/resources/json/ + relPath
     */
    private void setUpBlockCall(long blockNumber, String relPath) throws FileNotFoundException {
        final String path = rootPath + relPath;
        final String paramName = "height";

        Map<String, Object> param = new HashMap<>();
        param.put(paramName, blockNumber);
        ErisRPCRequestEntity entity = new ErisRPCRequestEntity(param, RPCMethod.GET_BLOCK);
        File file = new File(path);

        when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next());
    }

    private void setUpLatestBlockResponse(String relPath) throws FileNotFoundException {
        String path = rootPath + relPath;
        ErisRPCRequestEntity entity = new ErisRPCRequestEntity(null, RPCMethod.GET_LATEST_BLOCK);
        File file = new File(path);
        when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next());
    }

    private void setUpBlockRangeCall(long from, long to, String relPath) throws FileNotFoundException {
        String path = rootPath + relPath;

        Filters filters = new Filters();
        FilterHeight filterFrom = new FilterHeight(Operation.GREATER_OR_EQUALS, from);
        FilterHeight filterTo = new FilterHeight(Operation.LESS, to);
        filters.add(filterFrom);
        filters.add(filterTo);
        ErisRPCRequestEntity entity = new ErisRPCRequestEntity(filters.getMap(), RPCMethod.GET_BLOCKS);
        File file = new File(path);
        when(httpRpcClient.call(entity)).thenReturn(new Scanner(file).useDelimiter("\\Z").next().replaceAll("\\n ", ""));

    }

}