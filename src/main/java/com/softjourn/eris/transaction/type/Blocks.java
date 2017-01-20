package com.softjourn.eris.transaction.type;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This object returned by method RPCMethod.GET_BLOCKS "erisdb.getBlocks"
 * Created by vromanchuk on 19.01.17.
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Blocks {
    private Integer minHeight;
    private Integer maxHeight;
    private List<BlockMeta> blockMetas = new ArrayList<>();

    public List<BigInteger> getBlockNumbersWithTransaction() {
        return blockMetas.stream()
                .filter(Objects::nonNull)
                .filter(BlockMeta::haveTransaction)
                .map(BlockMeta::getHeader)
                .filter(Objects::nonNull)
                .map(Header::getHeight)
                .collect(Collectors.toList());
    }
}