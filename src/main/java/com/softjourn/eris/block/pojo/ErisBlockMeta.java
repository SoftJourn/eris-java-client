package com.softjourn.eris.block.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * BlockMeta describe Block class
 * This class returned when getting list of blocks
 * Created by vromanchuk on 19.01.17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class ErisBlockMeta {
    String hash;
    ErisBlockHeader header;

    boolean haveTransaction() {
        return header != null && header.getNumTxs() > 0;
    }
}
