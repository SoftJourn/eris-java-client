package com.softjourn.eris.transaction;

import com.softjourn.eris.block.pojo.ErisBlock;
import com.softjourn.eris.rpc.ErisRPCResponseEntity;
import com.softjourn.eris.transaction.parser.ErisParserService;
import com.softjourn.eris.transaction.parser.v11.Eris11CallTransactionParser;
import com.softjourn.eris.transaction.parser.v11.Eris11ParserService;
import com.softjourn.eris.transaction.parser.v12.Eris12CallTransactionParser;
import com.softjourn.eris.transaction.parser.v12.Eris12ParserService;
import com.softjourn.eris.transaction.pojo.ErisCallTransaction;
import com.softjourn.eris.transaction.pojo.ErisTransaction;
import com.softjourn.eris.transaction.pojo.ErisTransactionType;
import com.softjourn.eris.transaction.pojo.ErisUndefinedTransaction;
import lombok.extern.java.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.softjourn.eris.TestUtil.getStringFromFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Log
public class ErisTransactionServiceTest {

    private String abi;
    private ErisTransactionService transactionServiceV11;
    private ErisTransactionService transactionServiceV12;
    private ArrayList<ErisCallTransaction> transactions;
    private ArrayList<ErisUndefinedTransaction> undefinedTransactions;
    private ErisBlock blockV12;
    private ErisBlock blockV11;

    @Test
    public void visitTransactions() throws Exception {
        assertTrue(transactions.isEmpty());
        transactionServiceV12.visitTransactions(blockV12);
        assertEquals(1, transactions.size());
        assertEquals(0, undefinedTransactions.size());
    }

    @Test
    public void visitTransactionsV11() throws Exception {
        assertTrue(transactions.isEmpty());
        transactionServiceV11.visitTransactions(blockV11);
        assertEquals(1, transactions.size());
        assertEquals(0, undefinedTransactions.size());
    }

    @Before
    public void setUp() throws Exception {

        Eris12CallTransactionParser eris12CallTransactionParser = new Eris12CallTransactionParser();
        ErisParserService parserService = new Eris12ParserService(eris12CallTransactionParser);

        abi = getStringFromFile("json/coinsContractAbi.json");
        Function getAbi = address -> abi;

        transactions = new ArrayList<>();
        undefinedTransactions = new ArrayList<>();
        Map consumerMap = new HashMap<ErisTransactionType, Consumer<ErisTransaction>>() {{
            put(ErisTransactionType.CALL, transaction -> transactions.add((ErisCallTransaction) transaction));
            put(ErisTransactionType.UNDEFINED
                    , transaction -> undefinedTransactions.add((ErisUndefinedTransaction) transaction));
        }};
        transactionServiceV12 = new ErisTransactionService(parserService, consumerMap, getAbi);

        Eris11CallTransactionParser eris11CallTransactionParser = new Eris11CallTransactionParser();
        parserService = new Eris11ParserService(eris11CallTransactionParser);
        transactionServiceV11 = new ErisTransactionService(parserService, consumerMap, getAbi);

        String blockString;
        ErisRPCResponseEntity<ErisBlock> entity;

        blockString = getStringFromFile("json/v12/block3847.json");
        entity = new ErisRPCResponseEntity<>(blockString, ErisBlock.class);
        blockV12 = entity.getResult();

        blockString = getStringFromFile("json/v11/block3424287.json");
        entity = new ErisRPCResponseEntity<>(blockString, ErisBlock.class);
        blockV11 = entity.getResult();


    }
}
