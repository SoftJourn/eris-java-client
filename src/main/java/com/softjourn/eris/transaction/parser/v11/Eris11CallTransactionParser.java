package com.softjourn.eris.transaction.parser.v11;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.softjourn.eris.transaction.parser.ErisParser;
import com.softjourn.eris.transaction.pojo.ErisCallTransaction;
import com.softjourn.eris.transaction.pojo.ErisTransactionType;
import com.softjourn.eris.transaction.pojo.NotValidTransactionException;
import lombok.Data;

import java.util.ArrayList;


public class Eris11CallTransactionParser implements ErisParser {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ErisTransactionType getTransactionType() {
        return ErisTransactionType.CALL;
    }

    @Override
    public ErisCallTransaction parse(Object inputArray) throws NotValidTransactionException {
        if (inputArray instanceof ArrayNode) {
            return parse((ArrayNode) inputArray);
        }
        if (inputArray instanceof ArrayList) {
            return parse((ArrayList) inputArray);
        }
        throw new NotValidTransactionException("Type is not supported");
    }

    private ErisCallTransaction parse(ArrayList inputArray) throws NotValidTransactionException {
        ArrayNode arrayNode = objectMapper.convertValue(inputArray, ArrayNode.class);
        return parse(arrayNode);
    }

    private ErisCallTransaction parse(ArrayNode inputArray) throws NotValidTransactionException {
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
                    .functionName(getFunctionNameHash(transactionV11))
                    .build();

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
