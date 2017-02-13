package com.softjourn.eris.block.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.softjourn.eris.transaction.pojo.ClassifiableErisTransaction;
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
public class Block implements IBlock {

    private BlockData data;
    private BlockHeader header;

    @Override
    public Long getBlockNumber() {
        return header == null ? null : header.getHeight();
    }

    @Override
    public String getChainName() {
        return header == null ? null : header.getChainId();
    }

    @Override
    public LocalDateTime getTimeCreated() {
        return header == null ? null : header.getDateTime();
    }

    @Override
    public List<ClassifiableErisTransaction> getTransactions() {
        return data.getErisTransactions();
    }
}
