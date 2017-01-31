package com.softjourn.eris.transaction.pojo;

import com.fasterxml.jackson.databind.JsonNode;
import com.softjourn.eris.contract.ArgumentsDecoder;
import com.softjourn.eris.contract.ContractUnit;
import com.softjourn.eris.contract.Variable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.softjourn.eris.contract.Util.parseAbi;

/**
 * Transaction structure in Eris chain
 * Created by vromanchuk on 12.01.17.
 */
@Data
@AllArgsConstructor
public class ErisTransaction {

    private static final String DELIMITER = "0114";
    private static final String DELIMITER2 = "0144";
    private static final String SEQUENCE_END = "01";
    private static final Integer INT_SIZE_BYTES = 2;
    private static final String DEPLOY_MARKER = "6060604052";

    private String identifier;
    private String callerAddress;
    private String amount;
    private String sequence;
    private String transactionSignature;
    private String callerPubKey;
    private String contractAddress;
    private String gasLimit;
    private String fee;
    private String functionNameHash;
    private String callingData;
    private Boolean isDeploy;



    public ErisTransaction(String transactionString) throws IllegalArgumentException{
        boolean initialized = tryToInitializeWithJSON(transactionString);
        if(!initialized) {
            if (ErisTransaction.isDeployContractTx(transactionString)) {
                this.isDeploy = true;
                this.callingData = transactionString;
            } else {
                this.isDeploy = false;
                boolean binaryInitialication = tryToInitializeWithBinary(transactionString);
                if(!binaryInitialication)
                    throw new IllegalArgumentException("Illegal string. Format is not supported");
            }
        }
    }

    public String generateTxCode() {
        String result = this.identifier;
        result += ErisTransaction.DELIMITER;
        result += this.callerAddress;
        result += this.amount;
        result += ErisTransaction.getSizeHexString(this.sequence);
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

    @Data
    private static class ErisTransactionV11{
        @Data
        class Input{
            private String address;
            private Long amount;
            private Long sequence;
            private Object[] signature;
            private Object[] pub_key;
        }
        private Input input;
        private Long gas_limit;
        private Long fee;
        private String data;
        private String address;
    }

    /**
     * This transaction can occur in Eris version 12
     * @param textValue
     * @return true if initialization is successful. False otherwise.
     */
    private boolean tryToInitializeWithJSON(String textValue)  {
        try {
            JsonNode node = objectMapper.readTree(textValue);
            Integer txType = node.get(0).intValue();
            JsonNode transactionMetaData = node.get(1);
            ErisTransactionV11 erisTransactionV11 = objectMapper
                    .readValue(transactionMetaData.toString(),ErisTransactionV11.class);
            this.callingData = erisTransactionV11.getData();
            this.fee = toHexString(erisTransactionV11.getFee(),16);
            this.contractAddress = erisTransactionV11.getAddress();
            this.gasLimit = toHexString(erisTransactionV11.getGas_limit(),16);
            ErisTransactionV11.Input input = erisTransactionV11.getInput();
            this.transactionSignature = input.getSignature()[1].toString();
            this.callerPubKey = input.getPub_key()[1].toString();
            this.callerAddress = input.getAddress();
            this.sequence = toHexString(input.getSequence(),16);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * This transaction can occur in Eris version 12
     * @param transactionString
     * @return true if initialization is successful. False otherwise.
     */
    private boolean tryToInitializeWithBinary(String transactionString)  {

        try {
            // 4 digits of some identifier
            this.identifier = transactionString.substring(0, 4);
            // 4 digits of DELIMITER 0114
            this.callerAddress = transactionString.substring(8, 48);
            this.amount = transactionString.substring(48, 64);
            byte sequenceSize = Byte.valueOf(transactionString.substring(64, 66), 16);
            int shift = sequenceSize * 2;
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

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private static String toHexString(long i) {
        StringBuilder sb = new StringBuilder();
        sb.append(Long.toHexString(i).toUpperCase());
        if (sb.length() % 2 > 0) {
            sb.insert(0, '0'); // pad with leading zero if needed
        }
        return sb.toString();
    }


    private static String toHexString(long i, long size) {
        StringBuilder sb = new StringBuilder();
        sb.append(Long.toHexString(i).toUpperCase());
        long bits = size * 8;
        while (sb.length() < bits)
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
        int size = 1;
        while (i > 255) {
            i /= 256;
            size++;
        }
        return toHexString(size);
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

    public Long getAmountLongValue() throws NotValidTransactionException {
        try {
            return Long.valueOf(this.amount, 16);
        } catch (Exception e) {
            throw new NotValidTransactionException(e);
        }
    }

    public Long getSequenceLongValue() throws NotValidTransactionException {
        try {
            return Long.valueOf(this.sequence, 16);
        } catch (Exception e) {
            throw new NotValidTransactionException(e);
        }
    }

    public Long getGasLimitLongValue() throws NotValidTransactionException {
        try {
            return Long.valueOf(this.gasLimit, 16);
        } catch (Exception e) {
            throw new NotValidTransactionException(e);
        }
    }

    public Long getFeeLongValue() throws NotValidTransactionException {
        try {
            return Long.valueOf(this.fee, 16);
        } catch (Exception e) {
            throw new NotValidTransactionException(e);
        }
    }

    public static boolean isDeployContractTx(String transaction) {
        Pattern pattern = Pattern.compile(ErisTransaction.DEPLOY_MARKER + ".*" + ErisTransaction.DEPLOY_MARKER);
        return pattern.matcher(transaction).find();
    }

}
