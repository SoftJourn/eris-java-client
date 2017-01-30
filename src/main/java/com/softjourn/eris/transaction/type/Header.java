package com.softjourn.eris.transaction.type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Header of block in blockchain
 * Created by vromanchuk on 12.01.17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Header {

    //TODO change to naming strategy
    @JsonProperty(value = "chain_id")
    private String chainId;

    @JsonProperty(value = "num_txs")
    private Integer numTxs;

    private Long height;
    private LocalDateTime dateTime;

    @SuppressWarnings("unused")
    private void setTime(String time) {
        this.dateTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

}
