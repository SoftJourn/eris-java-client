package com.softjourn.eris.transaction.pojo;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * BlockData in Eris block
 * Created by vromanchuk on 12.01.17.
 */
@Data
public class BlockData {

    private List<ErisTransaction> erisTransactions;

    @SuppressWarnings("unused")
    @JsonSetter(value = "txs")
    private void setTransactions(List<Object> transactions) throws NotValidTransactionException {
        erisTransactions = new ArrayList<>();
        if (transactions != null) {
            for (Object tx:
                 transactions) {
                    erisTransactions.add(ErisTransactionCreator.create(tx));
            }
        }
    }

    public List<ErisTransaction> getErisTransactions() {
        return erisTransactions;
    }
}
