package com.softjourn.eris.transaction.parser;

import com.softjourn.eris.contract.ArgumentsDecoder;
import com.softjourn.eris.contract.ContractUnit;
import com.softjourn.eris.contract.Variable;
import com.softjourn.eris.transaction.pojo.ErisCallTransaction;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.softjourn.eris.contract.Util.parseAbi;

/**
 * Translate calling data to readable information about transaction with contract's abi file
 */
public class ErisCallDataTransactionParser {

    public static ErisCallTransaction parse(ErisCallTransaction transaction
            , Function<String, String> getAbiFromContractAddress){
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction is not specified");
        }
        if (transaction.getCallingData() == null) {
            throw new IllegalArgumentException("Calling data is not specified");
        }
        if (transaction.getContractAddress() == null || transaction.getContractAddress().isEmpty()) {
            throw new IllegalArgumentException("Contract address is not specified or this is deploy transaction");
        }
        String callingData = transaction.getCallingData();
        String contractAddress = transaction.getContractAddress();
        String abi = getAbiFromContractAddress.apply(contractAddress);
        if (abi == null) {
            throw new IllegalArgumentException("Can't find abi file to parseCallingData transaction");
        }
        String functionNameHash = getFunctionNameHash(callingData);
        ContractUnit unit = getContractUnit(functionNameHash,abi);
        String functionName = unit.getName();

        transaction.setFunctionArguments(parseCallingData(callingData,unit));
        transaction.setFunctionName(functionName);
        return transaction;
    }

    static Map<String, String> parseCallingData(String callingData, String abi) {
        String functionNameHash = getFunctionNameHash(callingData);
        ContractUnit unit = getContractUnit(functionNameHash,abi);
        return parseCallingData(callingData,unit);
    }

    private static Map<String, String> parseCallingData(String callingData, ContractUnit unit){
        ArgumentsDecoder argumentsDecoder = new ArgumentsDecoder();
        List<Object> inputArgs = argumentsDecoder.readInputArgs(unit, callingData.substring(8));
        Variable[] inputParams = unit.getInputs();
        if (inputParams.length != inputArgs.size())
            throw new IllegalArgumentException("Incorrect contract unit. Length of contactUnit inputs and values are different");
        Map<String, String> parsedCallingData = new HashMap<>();
        for (int i = 0; i < inputParams.length; i++) {
            parsedCallingData.put(inputParams[i].getName(), inputArgs.get(i).toString());
        }
        return parsedCallingData;
    }

    private static ContractUnit getContractUnit(String functionNameHash, String abi) {
        Map<String, ContractUnit> contractUnitHashMap;
        try {
            contractUnitHashMap = parseAbi(abi);
        } catch (IOException e) {
            throw new IllegalArgumentException("Abi file is not well formatted. Can't get contract units from it");
        }
        Map<String, String> hashFunctionMap = new HashMap<>(contractUnitHashMap.size());
        contractUnitHashMap.forEach((s, contractUnit) -> hashFunctionMap.put(contractUnit.signature(), s));
        String name = hashFunctionMap.get(functionNameHash.toLowerCase());
        if (name == null) {
            throw new IllegalArgumentException("Transaction refers to another abi file");
        }
        return contractUnitHashMap.get(name);
    }

    private static String getFunctionNameHash(String callingData) {
        try{
            return callingData.substring(0,8);
        } catch (Exception e){
            throw new IllegalArgumentException("Calling data is not well formatted. Can't get function name hash");
        }
    }

    private ErisCallDataTransactionParser(){

    }
}
