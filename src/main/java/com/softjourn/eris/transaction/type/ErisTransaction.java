package com.softjourn.eris.transaction.type;

import com.softjourn.eris.contract.ArgumentsDecoder;
import com.softjourn.eris.contract.ContractUnit;
import lombok.Data;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.softjourn.eris.contract.Util.parseAbi;

/**
 * Transaction structure in Eris chain
 * Created by vromanchuk on 12.01.17.
 */
@Data
public class ErisTransaction {

    private String identifier;
    private String amount;
    private String callerAddress;
    private String callerPubKey;
    private String contractAddress;
    private String callingData;
    private String hashCallingDataFunctionName;

    public ErisTransaction() {
    }

    public ErisTransaction(String transactionString) throws StringIndexOutOfBoundsException {
        // 4 digits of some identifier
        this.identifier = transactionString.substring(0, 4);
        // 4 digits of delimiter 0114
        this.callerAddress = transactionString.substring(8, 48);
        this.amount = transactionString.substring(48, 64);
        this.callerPubKey = transactionString.substring(200, 264);
        //delimiter 0114
        this.contractAddress = transactionString.substring(268, 308);
        // Some info gas_limit fee 0144 - some delimiter
        this.hashCallingDataFunctionName = transactionString.substring(344, 352);
        this.callingData = transactionString.substring(352);
    }

    public List<Object> parseCallingData(String abi) throws IOException {
        HashMap<String, ContractUnit> contractUnitHashMap = parseAbi(abi);

        ArgumentsDecoder argumentsDecoder = new ArgumentsDecoder();
        ContractUnit unit = contractUnitHashMap.get("mint");
        return argumentsDecoder.readInputArgs(unit, this.callingData);
    }

}
