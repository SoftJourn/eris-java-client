package com.softjourn.eris.accounts;

public class AccountCreatingException extends RuntimeException {

    public AccountCreatingException(Throwable cause) {
        super("Cant create account due to exception: " + cause.getMessage(), cause);
    }

    public AccountCreatingException(String message) {
        super(message);
    }
}
