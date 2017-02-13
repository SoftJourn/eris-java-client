package com.softjourn.eris.transaction.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * Created by vromanchuk on 10.02.17.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErisCallTransaction extends ClassifiableErisTransaction implements ITxInput {
    { transactionType = ErisTransactionType.CALL; }

    private String callerAddress;
    private Long amount;
    private Long sequence;
    private String signature;
    private String callerPubKey;
    private String contractAddress;
    private Long gasLimit;
    private Long fee;
    private String functionNameHash;
    private String callingData;
    private Boolean isDeploy;

}
