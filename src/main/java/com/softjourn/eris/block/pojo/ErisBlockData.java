package com.softjourn.eris.block.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * BlockData in Eris block
 * Created by vromanchuk on 12.01.17.
 */
@Data
class ErisBlockData implements BlockData {

    @JsonProperty(value = "txs")
    private List<Object> undefinedTransactions;

    public List<Object> getUndefinedTransactions() {
        return undefinedTransactions;
    }
}
