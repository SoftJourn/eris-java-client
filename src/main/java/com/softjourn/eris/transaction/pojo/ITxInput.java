package com.softjourn.eris.transaction.pojo;

/**
 * Parameters that were used to emmit transaction
 */
public interface ITxInput {
    String getCallerAddress();
    Long getAmount();
    Long getSequence();
    String getSignature();
    String getCallerPubKey();
}
