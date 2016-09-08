package com.softjourn.eris.contract.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Error {

    private final int code;

    private final String message;
}
