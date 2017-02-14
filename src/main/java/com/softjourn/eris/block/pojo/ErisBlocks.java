package com.softjourn.eris.block.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * This object returned by method RPCMethod.GET_BLOCKS "erisdb.getBlocks"
 * Created by vromanchuk on 19.01.17.
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ErisBlocks {

    private Integer minHeight;
    private Integer maxHeight;

    @Setter(AccessLevel.PACKAGE)
    @Getter(AccessLevel.PACKAGE)
    @JsonProperty("block_metas")
    @SuppressWarnings({"SpellCheckingInspection", "MismatchedQueryAndUpdateOfCollection"})
    private List<ErisBlockMeta> blockMetaList = new ArrayList<>();

    private static Stream<Long> getBlockNumbersWithTransaction(Stream<ErisBlockMeta> blockMetaStream) {
        return blockMetaStream
                .filter(Objects::nonNull)
                .filter(ErisBlockMeta::haveTransaction)
                .map(ErisBlockMeta::getHeader)
                .filter(Objects::nonNull)
                .map(ErisBlockHeader::getHeight);
    }

    public Stream<Long> getBlockNumbersWithTransaction() {
        return ErisBlocks.getBlockNumbersWithTransaction(blockMetaList.stream());
    }
}
