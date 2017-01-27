package com.softjourn.eris.transaction.type;

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

    private List<ErisTransaction> erisTransactions;

    @SuppressWarnings("unused")
    @JsonSetter(value = "txs")
    private void setTransactionsBites(List<String> transactionsBites) {
        if (this.erisTransactions == null) {
            this.erisTransactions = transactionsBites.stream().map(ErisTransaction::new).collect(Collectors.toList());
        }
    }

    public List<ErisTransaction> getErisTransactions() {
        return erisTransactions;
    }
}
