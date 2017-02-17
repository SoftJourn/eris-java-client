package com.softjourn.eris.transaction;

import com.softjourn.eris.block.pojo.BlockHeader;
import com.softjourn.eris.block.pojo.ErisBlock;
import com.softjourn.eris.transaction.parser.ErisCallDataTransactionParser;
import com.softjourn.eris.transaction.parser.ErisParserService;
import com.softjourn.eris.transaction.pojo.*;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * ErisTransactionService
 * Created by vromanchuk on 12.01.17.
 */
public class ErisTransactionService implements TransactionService {

    private ErisParserService parserService;
    private Function<String, String> getAbiFromContractAddress;
    private Map<ErisTransactionType, Consumer<? extends ErisTransaction>> consumerMap;

    public ErisTransactionService(ErisParserService parserService, Map<ErisTransactionType, Consumer<? extends ErisTransaction>> consumerMap
            , Function<String, String> getAbiFromContractAddress) {
        this.parserService = parserService;
        this.consumerMap = consumerMap;
        this.getAbiFromContractAddress = getAbiFromContractAddress;
    }


    @Override
    public void visitTransactions(ErisBlock block) {
        for (Object unParsedTx : block.getUndefinedTransactions()
                ) {
            BlockHeader header = block.getHeader();
            ErisUndefinedTransaction undefinedTransaction = new ErisUndefinedTransaction(unParsedTx, header);
            Consumer consumer;
            ErisTransaction transaction;
            try {
                transaction = parserService.parse(undefinedTransaction);
            } catch (NotValidTransactionException e) {
                transaction = undefinedTransaction;
            }

            if (transaction instanceof ErisCallTransaction) {
                ErisCallTransaction callTransaction = (ErisCallTransaction) transaction;
                ErisCallDataTransactionParser.parse(callTransaction, getAbiFromContractAddress);
            }

            consumer = consumerMap.get(transaction.getTransactionType());
            if (consumer != null) {
                consumer.accept(transaction);
            }

        }
    }
}
