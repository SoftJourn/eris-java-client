package com.softjourn.eris.transaction.type;

import com.softjourn.eris.contract.ArgumentsDecoder;
import com.softjourn.eris.contract.ContractUnit;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.softjourn.eris.contract.Util.parseAbi;

/**
 * Transaction in Eris chain
 * Created by vromanchuk on 12.01.17.
 */
public class Transaction {

    private final String transactionString;

    private final String identifier;
    private final String amount;
    private final String callerAddress;
    private final String callerPubKey;
    private final String contractAddress;
    private final String callingData;
    private final String hashCallingDataFunctionName;

    public Transaction(String transactionString) throws StringIndexOutOfBoundsException {
        this.transactionString = transactionString;
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

    public List<Object> parseCallingData() throws IOException {
        String abi = "[{\"constant\":false,\"inputs\":[{\"name\":\"spender\",\"type\":\"address\"},{\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"approve\",\"outputs\":[],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"accounts\",\"type\":\"address[]\"},{\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"distribute\",\"outputs\":[{\"name\":\"success\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"from\",\"type\":\"address\"},{\"name\":\"to\",\"type\":\"address\"},{\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"transferFrom\",\"outputs\":[{\"name\":\"success\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"_tokenColor\",\"type\":\"uint8\"}],\"name\":\"setColor\",\"outputs\":[],\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"tokenColor\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"spender\",\"type\":\"address\"},{\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"approveAndCall\",\"outputs\":[{\"name\":\"success\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"owner\",\"type\":\"address\"},{\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"mint\",\"outputs\":[],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"owner\",\"type\":\"address\"}],\"name\":\"balanceOf\",\"outputs\":[{\"name\":\"balance\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[],\"name\":\"getColor\",\"outputs\":[{\"name\":\"\",\"type\":\"uint8\"}],\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"receiver\",\"type\":\"address\"},{\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"transfer\",\"outputs\":[{\"name\":\"\",\"type\":\"bool\"}],\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"owner\",\"type\":\"address\"},{\"name\":\"spender\",\"type\":\"address\"}],\"name\":\"allowance\",\"outputs\":[{\"name\":\"remaining\",\"type\":\"uint256\"}],\"type\":\"function\"},{\"inputs\":[{\"name\":\"_tokenColor\",\"type\":\"uint8\"}],\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"from\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Transfer\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"from\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"to\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"Approval\",\"type\":\"event\"}]";
        HashMap<String, ContractUnit> contractUnitHashMap = parseAbi(abi);

        ArgumentsDecoder argumentsDecoder = new ArgumentsDecoder();
        ContractUnit unit = contractUnitHashMap.get("mint");
        return argumentsDecoder.readInputArgs(unit, this.callingData);
    }

    public String getTransactionString() {
        return transactionString;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getAmount() {
        return amount;
    }

    public String getCallerAddress() {
        return callerAddress;
    }

    public String getCallerPubKey() {
        return callerPubKey;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String getCallingData() {
        return callingData;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionString='" + transactionString + '\'' +
                ", identifier='" + identifier + '\'' +
                ", amount='" + amount + '\'' +
                ", callerAddress='" + callerAddress + '\'' +
                ", callerPubKey='" + callerPubKey + '\'' +
                ", contractAddress='" + contractAddress + '\'' +
                ", callingData='" + callingData + '\'' +
                '}';
    }
}
