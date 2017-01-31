package com.softjourn.eris.transaction.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.softjourn.eris.transaction.pojo.ErisTransaction;
import com.softjourn.eris.transaction.pojo.NotValidTransactionException;
import lombok.Data;

public class ErisTransactionParserObjectV11 implements IErisTransactionParser {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ErisTransaction parse(Object inputArray) throws NotValidTransactionException {
        if (inputArray instanceof ArrayNode) {
            ArrayNode input = (ArrayNode) inputArray;
            try {
                ErisTransactionV11 transactionV11 = objectMapper.treeToValue(input.get(1), ErisTransactionV11.class);
                return ErisTransaction.builder()
                        .txTypeCall((byte) input.get(0).asInt())
                        .gasLimit(transactionV11.getGas_limit())
                        .fee(transactionV11.getFee())
                        .callingData(transactionV11.data)
                        .contractAddress(transactionV11.address)
                        .callerAddress(transactionV11.getInput().getAddress())
                        .amount(transactionV11.getInput().getAmount())
                        .sequence(transactionV11.getInput().getSequence())
                        .transactionSignature(transactionV11.getInput().getSignature()[0].toString())
                        .callerPubKey(transactionV11.getInput().getPub_key()[0].toString())
                        .isDeploy(transactionV11.getAddress().isEmpty())
                        .build();

            } catch (JsonProcessingException | IndexOutOfBoundsException e) {
                throw new NotValidTransactionException("Unsupported format of input param", e);
            }
        } else {
            throw new NotValidTransactionException("Wrong instance of parse object required ArrayNode");
        }
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
