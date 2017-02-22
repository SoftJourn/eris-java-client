package com.softjourn.eris.transaction.parser.v12;

import com.softjourn.eris.block.pojo.BlockHeader;
import com.softjourn.eris.contract.Util;
import com.softjourn.eris.transaction.parser.AbstractErisCallTransactionParser;
import com.softjourn.eris.transaction.pojo.ErisCallTransaction;
import com.softjourn.eris.transaction.pojo.ErisTransactionType;
import com.softjourn.eris.transaction.pojo.ErisUndefinedTransaction;
import com.softjourn.eris.transaction.pojo.NotValidTransactionException;

import java.util.regex.Pattern;

import static com.softjourn.eris.transaction.parser.v12.Eris12CallTransactionParser.Structure.*;


public class Eris12CallTransactionParser extends AbstractErisCallTransactionParser {

    private static final Integer START = 0;
    private static final Integer HEX_BASE = 16;
    private static final Integer CHARS_IN_BYTE = 2;

    private static final String DEPLOY_MARKER = "6060604052";
    private static final String DELIMITER = "0114";
    private static final String DELIMITER2 = "0144";
    private static final String DELIMITER3 = "00";
    private static final String SEQUENCE_END = "01";

    enum Structure {
        TX_TYPE(Byte.BYTES),
        CALLER_ADDRESS(20),
        AMOUNT(Long.BYTES),
        SEQUENCE_SIZE(Byte.BYTES),
        SIGNATURE(64),
        PUB_KEY(32),
        CONTRACT_ADDRESS(20),
        GAS_LIMIT(Long.BYTES),
        FEE(Long.BYTES),
        DEPLOY_UNKNOWN_DATA(3);

        private int length;

        Structure(int bytes) {
            this.length = bytes * CHARS_IN_BYTE;
        }
    }

    @Override
    public ErisTransactionType getTransactionType() {
        return ErisTransactionType.CALL;
    }

    @Override
    public ErisCallTransaction parse(Object transaction) throws NotValidTransactionException {

        if (transaction instanceof String) {
            String inputString = (String) transaction;
            return parse(inputString).build();
        }

        if (transaction instanceof ErisUndefinedTransaction) {
            ErisUndefinedTransaction undefinedTransaction = (ErisUndefinedTransaction) transaction;
            return parse(undefinedTransaction.getBody(), undefinedTransaction.getBlockHeader());
        }
        throw new NotValidTransactionException("Type is not supported");
    }

    public ErisCallTransaction parse(Object transaction, BlockHeader header) throws NotValidTransactionException {
        if (transaction instanceof String) {
            String inputString = (String) transaction;
            ErisCallTransaction.ErisCallTransactionBuilder transactionBuilder = parse(inputString);
            transactionBuilder.blockHeader(header);
            ErisCallTransaction parsedTransaction = transactionBuilder.build();
            String txId = getTxId(parsedTransaction);
            parsedTransaction.setTxId(txId);
            return parsedTransaction;

        }
        throw new NotValidTransactionException("Type is not supported");
    }

    @Override
    protected String getTxId(String txJson) {
        return Util.tendermintTransactionV12RipeMd160Hash(txJson.getBytes()).toUpperCase();
    }

    private ErisCallTransaction.ErisCallTransactionBuilder parse(String inputString) throws NotValidTransactionException {
        int shift = START;
        try {

            checkTransactionType(inputString);

            shift += TX_TYPE.length;
            shift += SEQUENCE_END.length();
            shift += DELIMITER.length();

            ErisCallTransaction.ErisCallTransactionBuilder builder = ErisCallTransaction.builder();

            shift = parseTxInput(inputString, shift, builder);
            if (!isDeployTx(inputString)) {
                parseAsRegularTransaction(inputString, shift, builder);
            } else {
                parseAsDeployTransaction(inputString, shift, builder);
            }

            return builder;
        } catch (Exception e) {
            throw new NotValidTransactionException(e);
        }
    }

    private void checkTransactionType(String inputString) throws NotValidTransactionException {
        int shift = START;
        if (!Byte.valueOf(inputString.substring(shift, TX_TYPE.length), HEX_BASE)
                .equals(this.getTransactionType().getCode())) {
            throw new NotValidTransactionException("Type is not supported");
        }
    }

    private int parseTxInput(String inputString, int shift,
                             ErisCallTransaction.ErisCallTransactionBuilder builder) {
        builder.callerAddress(inputString.substring(shift, shift += CALLER_ADDRESS.length));

        String amount = inputString.substring(shift, shift += AMOUNT.length);
        builder.amount(Long.valueOf(amount, HEX_BASE));

        String sequenceSizeString = inputString.substring(shift, shift += SEQUENCE_SIZE.length);
        byte sequenceSize = Byte.valueOf(sequenceSizeString, HEX_BASE);

        String sequence = inputString.substring(shift, shift += sequenceSize * CHARS_IN_BYTE);
        builder.sequence(Long.valueOf(sequence, HEX_BASE));
        //SEQUENCE_END "01"
        shift += SEQUENCE_END.length();

        builder.signature(inputString.substring(shift, shift += SIGNATURE.length));
        //SEQUENCE_END "01"
        shift += SEQUENCE_END.length();

        builder.callerPubKey(inputString.substring(shift, shift += PUB_KEY.length));
        return shift;
    }

    private void parseAsRegularTransaction(String inputString, int shift,
                                           ErisCallTransaction.ErisCallTransactionBuilder builder) {
        //DELIMITER1 "0114"
        shift += DELIMITER.length();
        builder.contractAddress(inputString.substring(shift, shift += CONTRACT_ADDRESS.length));
        String gasLimit = inputString.substring(shift, shift += GAS_LIMIT.length);
        builder.gasLimit(Long.valueOf(gasLimit, HEX_BASE));
        String fee = inputString.substring(shift, shift += FEE.length);
        builder.fee(Long.valueOf(fee, HEX_BASE));
        // DELIMITER2 "0144"
        shift += DELIMITER2.length();
        builder.callingData(inputString.substring(shift));
        builder.isDeploy(false);
    }

    private void parseAsDeployTransaction(String inputStringEnding, int shift,
                                          ErisCallTransaction.ErisCallTransactionBuilder builder) {
        //TODO check if next byte is "00" for deploy builders. Next calculation expect that condition
        //DELIMITER3 "00"
        shift += DELIMITER3.length();
        builder.contractAddress("");
        String gasLimit = inputStringEnding.substring(shift, shift += GAS_LIMIT.length);
        builder.gasLimit(Long.valueOf(gasLimit, HEX_BASE));
        String fee = inputStringEnding.substring(shift, shift += FEE.length);
        builder.fee(Long.valueOf(fee, HEX_BASE));
        //TODO 3 bytes of unknown data Example: "0205C5"
        builder.functionName(inputStringEnding.substring(shift, shift += DEPLOY_UNKNOWN_DATA.length));
        builder.callingData(inputStringEnding.substring(shift));
        builder.isDeploy(true);
    }


    String generateOrigin(ErisCallTransaction transaction) {
        String result = toHexString(transaction.getTransactionType().getCode());
        result += SEQUENCE_END;
        result += DELIMITER;
        result += transaction.getCallerAddress();
        result += toHexString(transaction.getAmount());
        String sequenceSize = getSizeHexString(transaction.getSequence());
        result += sequenceSize;
        result += toHexString(transaction.getSequence(), Long.valueOf(sequenceSize));
        result += SEQUENCE_END;
        result += transaction.getSignature();
        result += SEQUENCE_END;
        result += transaction.getCallerPubKey();
        if (!transaction.getIsDeploy()) {
            result += DELIMITER;
            result += transaction.getContractAddress();
            result += toHexString(transaction.getGasLimit());
            result += toHexString(transaction.getFee());
            result += DELIMITER2;
            result += transaction.getCallingData();
        } else {
            result += DELIMITER3;
            result += toHexString(transaction.getGasLimit());
            result += toHexString(transaction.getFee());
            result += transaction.getFunctionName();
            result += transaction.getCallingData();
        }
        return result;
    }


    private boolean isDeployTx(String transaction) {
        Pattern pattern = Pattern.compile(DEPLOY_MARKER + ".*" + DEPLOY_MARKER);
        return pattern.matcher(transaction).find();
    }

    private static String toHexString(long i) {
        return toHexString(i, Long.BYTES);
    }

    private static String toHexString(byte i) {
        return toHexString(i, Byte.BYTES);
    }

    private static String toHexString(long i, long bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append(Long.toHexString(i).toUpperCase());
        long chars = CHARS_IN_BYTE * bytes;
        while (sb.length() < chars)
            sb.insert(0, '0');
        return sb.toString();
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
}
