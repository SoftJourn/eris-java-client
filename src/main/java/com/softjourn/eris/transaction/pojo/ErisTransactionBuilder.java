package com.softjourn.eris.transaction.pojo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ErisTransactionBuilder {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ErisTransaction parse(String inputString) throws NotValidTransactionException {
        try {
            JsonNode node = objectMapper.readTree(inputString);
        } catch (IOException e) {
            throw new NotValidTransactionException("Current transaction format isn't supported."
                    + "Available only eris version 11 and 12", e);
        }
        return null;
    }
}
