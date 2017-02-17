package com.softjourn.eris.block.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Block from eris block chain
 * Supported eris version in this class is 11 and 12
 * Created by vromanchuk on 12.01.17.
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErisBlock implements BlockHeader, BlockData {

    private ErisBlockData data;
    private ErisBlockHeader header;

    @Override
    public Long getBlockNumber() {
        return header == null ? null : header.getBlockNumber();
    }

    @Override
    public String getChainName() {
        return header == null ? null : header.getChainName();
    }

    @Override
    public LocalDateTime getTimeCreated() {
        return header == null ? null : header.getTimeCreated();
    }

    @Override
    public Integer getTransactionsNumber() {
        return header == null ? null : header.getTransactionsNumber();
    }

    @Override
    public String getDataHash() {
        return header == null ? null : header.getDataHash();
    }

    @Override
    public List<Object> getUndefinedTransactions() {
        return data == null ? null : data.getUndefinedTransactions();
    }
}
