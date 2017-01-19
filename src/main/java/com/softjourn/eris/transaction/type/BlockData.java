package com.softjourn.eris.transaction.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * BlockData in Eris block
 * Created by vromanchuk on 12.01.17.
 */
@Data
public class BlockData {

    @JsonProperty(value = "txs")
    private List<String> transactionsBites;

    private List<ErisTransaction> erisTransactions;

    public List<String> getTransactionsBites() {
        return transactionsBites;
    }

    @SuppressWarnings("unused")
    @JsonSetter(value = "txs")
    private void setTransactionsBites(List<String> transactionsBites) {
        if (this.erisTransactions == null) {
            this.transactionsBites = transactionsBites;
            this.erisTransactions = transactionsBites.stream().map(ErisTransaction::new).collect(Collectors.toList());
        }
    }

    public List<ErisTransaction> getErisTransactions() {
        return erisTransactions;
    }
}
