package com.softjourn.eris.block.pojo;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.softjourn.eris.transaction.pojo.ClassifiableErisTransaction;
import com.softjourn.eris.transaction.pojo.ErisTransactionCreator;
import com.softjourn.eris.transaction.pojo.NotValidTransactionException;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * BlockData in Eris block
 * Created by vromanchuk on 12.01.17.
 */
@Data
class BlockData {

    private List<ClassifiableErisTransaction> erisTransactions;

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

    public List<ClassifiableErisTransaction> getErisTransactions() {
        return erisTransactions;
    }
}
