package com.softjourn.eris.transaction.pojo;

/**
 * Parameters that were used to emmit body
 */
public interface TxInput {
    String getCallerAddress();
    Long getAmount();
    Long getSequence();
    String getSignature();
    String getCallerPubKey();
}
