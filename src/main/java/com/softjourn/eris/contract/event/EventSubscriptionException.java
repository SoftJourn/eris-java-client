package com.softjourn.eris.contract.event;

public class EventSubscriptionException extends RuntimeException {

    public EventSubscriptionException(Throwable cause) {
        super(cause);
    }

    public EventSubscriptionException(String message) {
        super(message);
    }

    public EventSubscriptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
