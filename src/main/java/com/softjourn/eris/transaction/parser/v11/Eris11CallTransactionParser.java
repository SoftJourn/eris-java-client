package com.softjourn.eris.transaction.parser.v11;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.softjourn.eris.block.pojo.BlockHeader;
import com.softjourn.eris.contract.Util;
import com.softjourn.eris.transaction.parser.AbstractErisCallTransactionParser;
import com.softjourn.eris.transaction.pojo.ErisCallTransaction;
import com.softjourn.eris.transaction.pojo.ErisUndefinedTransaction;
import com.softjourn.eris.transaction.pojo.NotValidTransactionException;
import lombok.Data;

import java.util.ArrayList;

import static com.softjourn.eris.transaction.pojo.ErisCallTransaction.ErisCallTransactionBuilder;

public class Eris11CallTransactionParser extends AbstractErisCallTransactionParser {
    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public ErisCallTransaction parse(Object transaction) throws NotValidTransactionException {
        if (transaction instanceof ErisUndefinedTransaction) {
            ErisUndefinedTransaction undefinedTransaction = (ErisUndefinedTransaction) transaction;
            BlockHeader blockHeader = undefinedTransaction.getBlockHeader();
            ErisCallTransaction result = parseArrays(undefinedTransaction.getBody()).blockHeader(blockHeader).build();
            if (blockHeader.getTransactionsNumber() > 1) {
                result.setTxId(this.getTxId(result));
            } else {
                result.setTxId(blockHeader.getDataHash());
            }
            return result;
        }
        ErisCallTransactionBuilder result = parseArrays(transaction);
        if (result != null) {
            return result.build();
        } else {
            throw new NotValidTransactionException("Type is not supported");
        }
    }

    private ErisCallTransactionBuilder parseArrays(Object transaction) throws NotValidTransactionException {
        if (transaction instanceof ArrayNode) {
            return parse((ArrayNode) transaction);
        }
        if (transaction instanceof ArrayList) {
            return parse((ArrayList) transaction);
        }
        return null;
    }

    @Override
    protected String getTxId(String txJson) {
        return Util.tendermintTransactionV11RipeMd160Hash(txJson.getBytes()).toUpperCase();
    }

    private ErisCallTransactionBuilder parse(ArrayList inputArray) throws NotValidTransactionException {
        ArrayNode arrayNode = objectMapper.convertValue(inputArray, ArrayNode.class);
        return parse(arrayNode);
    }

    private ErisCallTransactionBuilder parse(ArrayNode inputArray) throws NotValidTransactionException {
        try {

            if (inputArray.get(0).asInt() != this.getTransactionType().getCode()) {
                throw new NotValidTransactionException("Type is not supported");
            }
            ErisTransactionV11 transactionV11 = objectMapper.treeToValue(inputArray.get(1), ErisTransactionV11.class);
            return ErisCallTransaction.builder()
                    .gasLimit(transactionV11.getGas_limit())
                    .fee(transactionV11.getFee())
                    .callingData(transactionV11.data)
                    .contractAddress(transactionV11.address)
                    .callerAddress(transactionV11.getInput().getAddress())
                    .amount(transactionV11.getInput().getAmount())
                    .sequence(transactionV11.getInput().getSequence())
                    .signature(transactionV11.getInput().getSignature()[1].toString())
                    .callerPubKey(getPublicKey(transactionV11))
                    .isDeploy(transactionV11.getAddress().isEmpty())
                    .functionName(getFunctionNameHash(transactionV11));

        } catch (Exception e) {
            throw new NotValidTransactionException("Unsupported format of input param", e);
        }
    }

    private String getFunctionNameHash(ErisTransactionV11 txData) {
        if (!(txData.getAddress() == null || txData.getAddress().isEmpty())) {
            return txData.getData().substring(0, 8);
        }
        return null;
    }

    private String getPublicKey(ErisTransactionV11 tx) {
        return tx.getInput().getPub_key() == null ? "" : tx.getInput().getPub_key()[1].toString();
    }


    @Data
    private static class ErisTransactionV11 {
        @Data
        class Input {
            private String address;
            private Long amount;
            private Long sequence;
            private Object[] signature;
            private Object[] pub_key;
        }

        private Input input;
        private Long gas_limit;
        private Long fee;
        private String data;
        private String address;
    }
}
