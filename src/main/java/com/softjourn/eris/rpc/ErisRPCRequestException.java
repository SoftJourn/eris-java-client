package com.softjourn.eris.rpc;

public class ErisRPCRequestException extends RuntimeException {

    public ErisRPCRequestException() {
    }

    public ErisRPCRequestException(String s) {
        super(s);
    }

    public ErisRPCRequestException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ErisRPCRequestException(Throwable throwable) {
        super(throwable);
    }

    public ErisRPCRequestException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
