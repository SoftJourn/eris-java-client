package com.softjourn.eris.transaction.pojo;

import com.softjourn.eris.block.pojo.BlockHeader;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 *
 * Created by vromanchuk on 10.02.17.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class ErisCallTransaction extends ErisTransaction implements TxInput {

    private String callerAddress;
    private Long amount;
    private Long sequence;
    private String signature;
    private String callerPubKey;
    private String contractAddress;
    private Long gasLimit;
    private Long fee;
    private String callingData;
    private Boolean isDeploy;

    private String txId;
    private String functionName;
    private Map<String,String> functionArguments;

    @Builder(toBuilder = true)
    public ErisCallTransaction(BlockHeader blockHeader, String callerAddress, Long amount, Long sequence
            , String signature, String callerPubKey, String contractAddress, Long gasLimit, Long fee
            , String callingData, Boolean isDeploy , String txId, String functionName, Map<String,String> functionArguments) {
        super(ErisTransactionType.CALL, blockHeader);
        this.txId = txId;

        this.callerAddress = callerAddress;
        this.amount = amount;
        this.sequence = sequence;
        this.signature = signature;
        this.callerPubKey = callerPubKey;
        this.contractAddress = contractAddress;
        this.gasLimit = gasLimit;
        this.fee = fee;
        this.callingData = callingData;
        this.isDeploy = isDeploy;

        this.functionName = functionName;
        this.functionArguments = functionArguments;
    }

}
