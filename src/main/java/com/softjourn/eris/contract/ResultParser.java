package com.softjourn.eris.contract;

import com.fasterxml.jackson.databind.JsonNode;
import com.softjourn.eris.contract.response.ResponseParsingException;

import java.util.stream.StreamSupport;

public class ResultParser {

    /**
     * Returns single result object.
     * Eris 0.11.4 returns result object as array with single object included
     * Eris 0.12 returns just object
     *
     * @param resultObjectOrArray result node from response
     * @return Returns single result object.
     */
    public JsonNode getResultObject(JsonNode resultObjectOrArray) throws ResponseParsingException {
        if (resultObjectOrArray.isNull()) return null;
        if (!(resultObjectOrArray.isObject() || resultObjectOrArray.isArray()))
            throw new ResponseParsingException("Wrong response. Result field is not object pojo.");
        JsonNode result = null;
        if (resultObjectOrArray.isArray()) {
            result = StreamSupport.stream(resultObjectOrArray.spliterator(), false)
                    .filter(JsonNode::isObject)
                    .findFirst().orElse(null);
        } else if (resultObjectOrArray.isObject()) {
            return resultObjectOrArray;
        }
        if (result == null) {
            throw new ResponseParsingException("Wrong response. Result field is not presented.");
        } else {
            return result;
        }
    }

}
