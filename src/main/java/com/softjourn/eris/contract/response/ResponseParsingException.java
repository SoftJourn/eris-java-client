package com.softjourn.eris.contract.response;

import java.io.IOException;

public class ResponseParsingException extends IOException {

    public ResponseParsingException(String message) {
        super(message);
    }
}
