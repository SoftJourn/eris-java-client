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

    private static final String DELIMITER = "0114";

    private String identifier;
    private String callerAddress;
    private String amount;
    private String unknownData;
    private String callerPubKey;
    private String contractAddress;
    private String additionalInfo;
    private String functionNameHash;
    private String callingData;

    public ErisTransaction(String transactionString) throws StringIndexOutOfBoundsException {
        // 4 digits of some identifier
        this.identifier = transactionString.substring(0, 4);
        // 4 digits of DELIMITER 0114
        this.callerAddress = transactionString.substring(8, 48);
        this.amount = transactionString.substring(48, 64);
        this.unknownData = transactionString.substring(64, 200);
        this.callerPubKey = transactionString.substring(200, 264);
        //delimiter 0114
        this.contractAddress = transactionString.substring(268, 308);
        // Some info gas_limit fee 0144 - some delimiter
        this.additionalInfo = transactionString.substring(308, 344);
        this.functionNameHash = transactionString.substring(344, 352);
        this.callingData = transactionString.substring(352);
    }

    public List<Object> parseCallingData(String abi) throws IOException {
        HashMap<String, ContractUnit> contractUnitHashMap = parseAbi(abi);

        ArgumentsDecoder argumentsDecoder = new ArgumentsDecoder();
        ContractUnit unit = contractUnitHashMap.get("mint");
        return argumentsDecoder.readInputArgs(unit, this.callingData);
    }

    public String generateTxCode() {
        String result = this.identifier;
        result += ErisTransaction.DELIMITER;
        result += this.callerAddress;
        result += this.amount;
        result += this.unknownData;
        result += this.callerPubKey;
        result += ErisTransaction.DELIMITER;
        result += this.contractAddress;
        result += this.additionalInfo;
        result += this.functionNameHash;
        result += this.callingData;
        return result;
    }
}
