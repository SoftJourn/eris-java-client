package com.softjourn.eris.contract;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.softjourn.eris.contract.response.*;
import com.softjourn.eris.contract.response.Error;
import com.softjourn.eris.contract.types.Type;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public class ResponseParser<T> implements Function<String, Response<T>> {

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
        ObjectReader reader = mapper.readerFor(TxParams.class);

        return Optional.ofNullable(res)
                .flatMap(r -> Optional.ofNullable(r.get("result")))
                .flatMap(r -> Optional.ofNullable(getResultObject(r)))
                .flatMap(r -> Optional.ofNullable((TxParams)valueByNode(r, reader)))
                .filter(tx -> !(tx.getTxId() == null && tx.getOrigin() == null))
                .orElse(null);
    }

    @Override
    public Response<T> apply(String s) {
        try {
            return parse(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ReadException extends RuntimeException {
    }

    private <V> V valueByNode(JsonNode node, ObjectReader reader) {
        try {
            return reader.readValue(node);
        } catch (IOException e) {
            throw new ReadException();
        }
    }

    private Error getError(JsonNode res) throws IOException {
        if (!res.has("error")) throw new ResponseParsingException("Wrong response. Error field is not presented.");
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
        JsonNode result = getResultObject(res.get("result"));

        return Optional.ofNullable(result)
                .flatMap(r -> Optional.ofNullable(r.get("return")))
                .map(JsonNode::asText)
                .map(this::mapReturnString)
                .orElse(null);
    }

    private ReturnValue<T> mapReturnString(String value) {
        if (!outVariable.getType().canRepresent(value)) {
            throw new ResponseParsingException("Wrong response. " +
                    "Value " + value + " can't be represented by " +
                    "required type " + outVariable.getType().toString() + ".");
        } else {
            Type<T> type = outVariable.getType();
            return new ReturnValue<>(type.valueClass(), type.formatOutput(value));
        }
    }

    /**
     * Returns single result object.
     * Eris 0.11.4 returns result object as array with single object included
     * Eris 0.12 returns just object
     *
     * @param resultObjectOrArray result node from response
     * @return Returns single result object.
     */
    private JsonNode getResultObject(JsonNode resultObjectOrArray) throws ResponseParsingException {
        if (resultObjectOrArray.isNull()) return null;
        if (!(resultObjectOrArray.isObject() || resultObjectOrArray.isArray()))
            throw new ResponseParsingException("Wrong response. Result field is not object type.");
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
