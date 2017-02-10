package com.softjourn.eris.transaction.pojo;

import com.softjourn.eris.contract.ArgumentsDecoder;
import com.softjourn.eris.contract.ContractUnit;
import com.softjourn.eris.contract.Variable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErisTransaction {

    private static final String DELIMITER = "0114";
    private static final String DELIMITER2 = "0144";
    private static final String DELIMITER3 = "00";
    private static final String SEQUENCE_END = "01";
    private static final Integer CHARS_IN_BYTE = 2;

    private Byte txTypeCall;
    private String callerAddress;
    private Long amount;
    private Long sequence;
    private String transactionSignature;
    private String callerPubKey;
    private String contractAddress;
    private Long gasLimit;
    private Long fee;
    private String functionNameHash;
    private String callingData;
    private Boolean isDeploy;

    public String generateTxCode() {
        String result = toHexString(this.txTypeCall);
        result += SEQUENCE_END;
        result += DELIMITER;
        result += this.callerAddress;
        result += toHexString(this.amount);
        result += getSizeHexString(this.sequence);
        result += toHexString(this.sequence,Long.valueOf(getSizeHexString(this.sequence)));
        result += SEQUENCE_END;
        result += this.transactionSignature;
        result += SEQUENCE_END;
        result += this.callerPubKey;
        if(!isDeploy) {
            result += DELIMITER;
            result += this.contractAddress;
            result += toHexString(this.gasLimit);
            result += toHexString(this.fee);
            result += DELIMITER2;
            result += this.callingData;
        } else {
            result += DELIMITER3;
            result += toHexString(this.gasLimit);
            result += toHexString(this.fee);
            result += this.functionNameHash;
            result += this.callingData;
        }
        return result;
    }

    private static String toHexString(long i) {
        return toHexString(i,Long.BYTES);
    }

    private static String toHexString(byte i) {
        return toHexString(i,Byte.BYTES);
    }

    private static String toHexString(long i, long bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append(Long.toHexString(i).toUpperCase());
        long chars = CHARS_IN_BYTE * bytes;
        while (sb.length() < chars)
            sb.insert(0, '0');
        return sb.toString();
    }

    private static String getSizeHexString(String hexString) {
        if (hexString == null || hexString.isEmpty())
            return "";
        return toHexString(hexString.length() / 2);
    }

    private static String getSizeHexString(long i) {
        if (i < 0)
            return "";
        byte size = 1;
        while (i > 255) {
            i /= 256;
            size++;
        }
        return toHexString(size);
    }

    public Map<String, String> parseCallingData(ContractUnit unit) {
        ArgumentsDecoder argumentsDecoder = new ArgumentsDecoder();
        List<Object> inputArgs = argumentsDecoder.readInputArgs(unit, this.callingData.substring(8));
        Variable[] inputParams = unit.getInputs();
        if (inputParams.length != inputArgs.size())
            throw new IllegalArgumentException("Incorrect contract unit. Length of contactUnit inputs and values are different");
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
