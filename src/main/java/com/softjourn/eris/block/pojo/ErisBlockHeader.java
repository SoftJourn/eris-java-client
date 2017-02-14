package com.softjourn.eris.block.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * BlockHeader of block in blockchain
 * Created by vromanchuk on 12.01.17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ErisBlockHeader implements BlockHeader {

    private String chainId;
    private Integer numTxs;
    private Long height;
    private LocalDateTime dateTime;
    private String dataHash;

    @SuppressWarnings("unused")
    private void setTime(String time) {
        this.dateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @Override
    public Long getBlockNumber() {
        return height;
    }

    @Override
    public String getChainName() {
        return chainId;
    }

    @Override
    public LocalDateTime getTimeCreated() {
        return dateTime;
    }

    @Override
    public Integer getTransactionsNumber() {
        return numTxs;
    }
}
