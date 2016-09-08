package com.softjourn.eris.contract;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.softjourn.eris.contract.response.*;
import com.softjourn.eris.contract.response.Error;
import com.softjourn.eris.contract.types.Type;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public class ResponseParser<T> implements Function<String, Response<T>>{

    private ObjectMapper mapper;

    private Variable<T> outVariable;


    public ResponseParser(Variable<T> outVar) {
        mapper = new ObjectMapper();
        outVariable = outVar;
    }


    public Response<T> parse(String responseBody) throws IOException {
        JsonNode res = mapper.readTree(responseBody);

        String id = getId(res);
        ReturnValue<T> returnValue = getReturnValue(res);
        Error error = getError(res);
        TxParams txParams = getTxParams(res);

        return new Response<>(id, returnValue, error, txParams);
    }

    private TxParams getTxParams(JsonNode res) throws ResponseParsingException {
        JsonNode result = res.get("result");
        if (result == null) throw new ResponseParsingException("Wrong response. Result field is not presented.");
        if (result.isNull()) return null;
        if (! (result.isObject() || result.isArray())) throw new ResponseParsingException("Wrong response. Result field is neither object nor array type.");

        ObjectReader reader = mapper.readerFor(TxParams.class);
        if (result.isArray()) {
            return StreamSupport.stream(result.spliterator(), false)
                    .filter(JsonNode::isObject)
                    .map(n -> this.<TxParams> valueByNode(n, reader))
                    .findFirst().orElse(null);
        }

        return null;
    }

    @Override
    public Response<T> apply(String s) {
        try {
            return parse(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ReadException extends RuntimeException {}

    private <V> V valueByNode(JsonNode node, ObjectReader reader) {
        try {
            return reader.readValue(node);
        } catch (IOException e) {
            throw new ReadException();
        }
    }

    private Error getError(JsonNode res) throws IOException {
        if (! res.has("error")) throw new ResponseParsingException("Wrong response. Error field is not presented.");
        JsonNode error = res.get("error");
        ObjectReader reader = mapper.readerFor(Error.class);
        return reader.readValue(error);
    }

    private String getId(JsonNode res) throws ResponseParsingException {
        String id = res.get("id").asText();
        if (id == null) throw new ResponseParsingException("Wrong response. Id field is not presented.");
        return id;
    }

    @SuppressWarnings("unchecked")
    private ReturnValue<T> getReturnValue(JsonNode res) throws ResponseParsingException {
        if (outVariable == null) return null;
        JsonNode result = res.get("result");
        if (result.isNull()) return null;
        if (! (result.isObject() || result.isArray())) throw new ResponseParsingException("Wrong response. Result field is not object type.");

        if (result.isArray()) {
            result = StreamSupport.stream(result.spliterator(), false)
                    .filter(JsonNode::isObject)
                    .findFirst().orElse(null);
        }

        if (result == null) throw new ResponseParsingException("Wrong response. Result field is not presented.");

        JsonNode retVal = result.get("return");
        if (retVal == null) throw new ResponseParsingException("Wrong response. Return value expected but not presented.");
        String retString = retVal.asText();
        if (! outVariable.getType().canRepresent(retString))
            throw new ResponseParsingException("Wrong response. " +
                    "Value " + retString + " can't be represented by " +
                    "required type " + outVariable.getType().toString() + ".");

        Type<T> type = outVariable.getType();
        return new ReturnValue<>(type.valueClass(), type.formatOutput(retString));
    }
}
