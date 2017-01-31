package com.softjourn.eris.transaction.pojo;

public class NotValidTransactionException extends Exception{
    public NotValidTransactionException() {
    }

    public NotValidTransactionException(String s) {
        super(s);
    }

    public NotValidTransactionException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NotValidTransactionException(Throwable throwable) {
        super(throwable);
    }

    public NotValidTransactionException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
