package com.softjourn.eris.transaction.type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * BlockMeta describe Block class
 * This class returned when getting list of blocks
 * Created by vromanchuk on 19.01.17.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockMeta {
    String hash;
    Header header;

    public boolean haveTransaction() {
        return header.getNumTxs() > 0;
    }
}
