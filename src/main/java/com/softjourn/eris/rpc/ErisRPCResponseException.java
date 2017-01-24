package com.softjourn.eris.rpc;

public class ErisRPCResponseException extends RuntimeException {

    public ErisRPCResponseException() {
    }

    public ErisRPCResponseException(String s) {
        super(s);
    }

    public ErisRPCResponseException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ErisRPCResponseException(Throwable throwable) {
        super(throwable);
    }

    public ErisRPCResponseException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
