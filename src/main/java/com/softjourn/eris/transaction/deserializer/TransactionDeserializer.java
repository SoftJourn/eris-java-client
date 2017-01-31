package com.softjourn.eris.transaction.deserializer;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.softjourn.eris.transaction.pojo.ErisTransaction;

import java.io.IOException;
import java.util.List;

public class TransactionDeserializer extends JsonDeserializer<List<ErisTransaction>> {
    @Override
    public List<ErisTransaction> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
//        ObjectCodec oc = p.getCodec();
//        JsonNode node = oc.readTree(p);
//        List<ErisTransaction> data = new ArrayList<>();
//        node.forEach(jsonNode -> {
//            String s;
//            boolean isArray = jsonNode.isArray();
//            if(isArray){
//                 s = jsonNode.toString();
//            } else {
//                s = jsonNode.textValue();
//            }
//            ErisTransaction transaction = new ErisTransaction(s);
//            data.add(transaction);
//        });
//        return data;
        return null;
    }
}
