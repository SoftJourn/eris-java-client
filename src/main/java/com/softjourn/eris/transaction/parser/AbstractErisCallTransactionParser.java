package com.softjourn.eris.transaction.parser;

import com.softjourn.eris.transaction.pojo.ErisCallTransaction;
import com.softjourn.eris.transaction.pojo.ErisTransactionType;
import com.softjourn.eris.transaction.pojo.TxInput;

public abstract class AbstractErisCallTransactionParser implements ErisParser{

    @Override
    public ErisTransactionType getTransactionType() {
        return ErisTransactionType.CALL;
    }

    private String getTxJson(String chainId, String contractAddress, String txData, long fee, long gasLimit, String txInput) {
        return "{\"chain_id\":\"" + chainId + "\","
                + "\"tx\":[" + getTransactionType().getCode()
                + ",{\"address\":\"" + contractAddress
                + "\",\"data\":\"" + txData
                + "\"," + "\"fee\":"
                + fee + ",\"gas_limit\":"
                + gasLimit + ",\"input\":"
                + txInput + "" + "}]}";
    }

    private String getTxJson(String chainId, ErisCallTransaction transaction){

        String txInputJson = getTxInputJson(transaction);
        return getTxJson(chainId,transaction.getContractAddress(), transaction.getCallingData()
                ,transaction.getFee(),transaction.getGasLimit(),txInputJson);
    }

    private String getTxInputJson(String userAddress, long amount, long sequence) {
        return "{\"address\":\"" + userAddress + "\",\"amount\":" + amount + ",\"sequence\":" + sequence + "}";
    }

    private String getTxInputJson(TxInput transaction){
        return getTxInputJson(transaction.getCallerAddress(),transaction.getAmount(),transaction.getSequence());
    }


    public String getTxId(ErisCallTransaction transaction){
        String txJson = getTxJson(transaction.blockHeader.getChainName(),transaction);
        return getTxId(txJson);
    }

    protected abstract String getTxId(String txJson);
}
