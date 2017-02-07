package com.softjourn.eris.transaction.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.softjourn.eris.transaction.pojo.ErisTransaction;
import com.softjourn.eris.transaction.pojo.NotValidTransactionException;
import lombok.Data;

import java.util.ArrayList;

public class ErisTransactionParserObjectV11 implements IErisTransactionParser {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ErisTransaction parse(Object inputArray) throws NotValidTransactionException {
        if (inputArray instanceof ArrayNode) {
            ArrayNode input = (ArrayNode) inputArray;
            return parse(input);
        } else {
            if (inputArray instanceof ArrayList) {
                ArrayList input = (ArrayList) inputArray;
                return parse(input);
            } else {
                throw new NotValidTransactionException("Wrong instance of parse object required ArrayList or ArrayNode");
            }
        }
    }

    public ErisTransaction parse(ArrayList inputArray) throws NotValidTransactionException {
        ArrayNode arrayNode = objectMapper.convertValue(inputArray, ArrayNode.class);
        return parse(arrayNode);
    }

    public ErisTransaction parse(ArrayNode inputArray) throws NotValidTransactionException {
        try {
            ErisTransactionV11 transactionV11 = objectMapper.treeToValue(inputArray.get(1), ErisTransactionV11.class);
            return ErisTransaction.builder()
                    .txTypeCall((byte) inputArray.get(0).asInt())
                    .gasLimit(transactionV11.getGas_limit())
                    .fee(transactionV11.getFee())
                    .callingData(transactionV11.data)
                    .contractAddress(transactionV11.address)
                    .callerAddress(transactionV11.getInput().getAddress())
                    .amount(transactionV11.getInput().getAmount())
                    .sequence(transactionV11.getInput().getSequence())
                    .transactionSignature(transactionV11.getInput().getSignature()[0].toString())
                    .callerPubKey(getPublickKey(transactionV11))
                    .isDeploy(transactionV11.getAddress().isEmpty())
                    .build();

        } catch (JsonProcessingException | IndexOutOfBoundsException e) {
            throw new NotValidTransactionException("Unsupported format of input param", e);
        }
    }

    private String getPublickKey(ErisTransactionV11 tx) {
        return tx.getInput().getPub_key() == null ? "" : tx.getInput().getPub_key()[0].toString();
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
