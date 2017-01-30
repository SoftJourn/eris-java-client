package com.softjourn.eris.transaction.deserializer;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.softjourn.eris.transaction.type.BlockData;
import com.softjourn.eris.transaction.type.ErisTransaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlockDataDeserializer extends JsonDeserializer<BlockData> {
    @Override
    public BlockData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec oc = p.getCodec();
        JsonNode node = oc.readTree(p);
        BlockData blockData = new BlockData();
        List<ErisTransaction> data = new ArrayList<>();
        ArrayNode array = (ArrayNode) node.get("txs");
        array.forEach(jsonNode -> {
            String s;
            boolean isArray = jsonNode.isArray();
            if(isArray){
                 s = jsonNode.toString();
            } else {
                s = jsonNode.textValue();
            }
            ErisTransaction transaction = new ErisTransaction(s);
            data.add(transaction);
        });
        blockData.setErisTransactions(data);
        return blockData;
    }
}
