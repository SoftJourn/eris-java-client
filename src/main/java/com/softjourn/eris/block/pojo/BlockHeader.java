package com.softjourn.eris.block.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * BlockHeader of block in blockchain
 * Created by vromanchuk on 12.01.17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockHeader {

    //TODO change to naming strategy
    @JsonProperty(value = "chain_id")
    private String chainId;

    @JsonProperty(value = "num_txs")
    private Integer numTxs;

    private Long height;
    private LocalDateTime dateTime;
    @JsonProperty("data_hash")
    private String dataHash;

    @SuppressWarnings("unused")
    private void setTime(String time) {
        this.dateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

}
