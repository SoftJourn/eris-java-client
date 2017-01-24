package com.softjourn.eris.transaction.type;

import com.softjourn.eris.contract.ArgumentsDecoder;
import com.softjourn.eris.contract.ContractUnit;
import com.softjourn.eris.contract.Variable;
import lombok.Data;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.softjourn.eris.contract.Util.parseAbi;

/**
 * Transaction structure in Eris chain
 * Created by vromanchuk on 12.01.17.
 */
@Data
public class ErisTransaction {

    private static final String DELIMITER = "0114";
    private static final String DELIMITER2 = "0144";
    private static final String SEQUENCE_END = "01";

    private String identifier;
    private String callerAddress;
    private String amount;
    private String sequenceSize;
    private String sequence;
    private String transactionSignature;
    private String callerPubKey;
    private String contractAddress;
    private String gasLimit;
    private String fee;
    private String functionNameHash;
    private String callingData;

    public ErisTransaction(String transactionString) throws StringIndexOutOfBoundsException {
        // 4 digits of some identifier
        this.identifier = transactionString.substring(0, 4);
        // 4 digits of DELIMITER 0114
        this.callerAddress = transactionString.substring(8, 48);
        this.amount = transactionString.substring(48, 64);
        this.sequenceSize = transactionString.substring(64, 66);
        int shift = new Byte(this.sequenceSize) * 2;
        shift += 66;
        this.sequence = transactionString.substring(66, shift);
        //SEQUENCE_END "01"
        shift += 2;
        this.transactionSignature = transactionString.substring(shift, shift + 128);
        shift += 128;
        //SEQUENCE_END "01"
        shift += 2;
        this.callerPubKey = transactionString.substring(shift, shift + 64);
        shift += 64;
        //DELIMITER1 "0114"
        shift += 4;
        this.contractAddress = transactionString.substring(shift, shift + 40);
        shift += 40;
        this.gasLimit = transactionString.substring(shift, shift + 16);
        shift += 16;
        this.fee = transactionString.substring(shift, shift + 16);
        shift += 16;
        // DELIMITER2 "0144"
        shift += 4;
        this.functionNameHash = transactionString.substring(shift, shift + 8);
        shift += 8;
        this.callingData = transactionString.substring(shift);
    }

    public String generateTxCode() {
        String result = this.identifier;
        result += ErisTransaction.DELIMITER;
        result += this.callerAddress;
        result += this.amount;
        result += this.sequenceSize;
        result += this.sequence;
        result += ErisTransaction.SEQUENCE_END;
        result += this.transactionSignature;
        result += ErisTransaction.SEQUENCE_END;
        result += this.callerPubKey;
        result += ErisTransaction.DELIMITER;
        result += this.contractAddress;
        result += this.gasLimit;
        result += this.fee;
        result += ErisTransaction.DELIMITER2;
        result += this.functionNameHash;
        result += this.callingData;
        return result;
    }

    public Map<String, String> parseCallingData(ContractUnit unit) {
        ArgumentsDecoder argumentsDecoder = new ArgumentsDecoder();
        List<Object> inputArgs = argumentsDecoder.readInputArgs(unit, this.callingData);
        Variable[] inputParams = unit.getInputs();
        if (inputParams.length != inputArgs.size())
            throw new IllegalArgumentException("Incorrect contract unit. " +
                    "Length of contactUnit inputs and values are different");
        Map<String, String> callingData = new HashMap<>();
        for (int i = 0; i < inputParams.length; i++) {
            callingData.put(inputParams[i].getName(), inputArgs.get(i).toString());
        }
        return callingData;
    }

    public ContractUnit getContractUnit(String abi) throws IOException {
        Map<String, ContractUnit> contractUnitHashMap = parseAbi(abi);
        Map<String, String> hashFunctionMap = new HashMap<>(contractUnitHashMap.size());
        contractUnitHashMap.forEach((s, contractUnit) -> hashFunctionMap.put(contractUnit.signature(), s));
        String name = hashFunctionMap.get(this.functionNameHash.toLowerCase());
        if (name == null) {
            throw new IllegalArgumentException("Wrong abi file. Transaction refers to another");
        }
        return contractUnitHashMap.get(name);
    }
}
