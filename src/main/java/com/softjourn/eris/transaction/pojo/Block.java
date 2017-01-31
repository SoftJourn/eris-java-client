package com.softjourn.eris.transaction.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Block from eris block chain
 * Created by vromanchuk on 12.01.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Block {

    private BlockData data;
    private Header header;

}
