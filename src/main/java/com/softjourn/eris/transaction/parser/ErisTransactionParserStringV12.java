package com.softjourn.eris.transaction.parser;

import com.softjourn.eris.transaction.pojo.ErisTransaction;
import com.softjourn.eris.transaction.pojo.NotValidTransactionException;

import java.util.regex.Pattern;


public class ErisTransactionParserStringV12 implements IErisTransactionParser {

    private static final String DEPLOY_MARKER = "6060604052";


    @Override
    public ErisTransaction parse(Object input) throws NotValidTransactionException {
        if (input instanceof String) {
            String inputString = input.toString();
            try {
                ErisTransaction transaction =
                        ErisTransaction.builder()
                                // 4 digits of some identifier
                                .txTypeCall(Byte.valueOf(inputString.substring(0, 2),16))
                                .callerAddress(inputString.substring(8, 48))
                                .amount(Long.valueOf(inputString.substring(48, 64), 16)).build();
                byte sequenceSize = Byte.valueOf(inputString.substring(64, 66), 16);
                int shift = sequenceSize * 2;
                shift += 66;
                transaction.setSequence(Long.valueOf(inputString.substring(66, shift), 16));
                //SEQUENCE_END "01"
                shift += 2;
                transaction.setTransactionSignature(inputString.substring(shift, shift + 128));
                shift += 128;
                //SEQUENCE_END "01"
                shift += 2;
                transaction.setCallerPubKey(inputString.substring(shift, shift + 64));
                shift += 64;

                if (!isDeployTx(inputString)) {
                    //DELIMITER1 "0114"
                    shift += 4;
                    transaction.setContractAddress(inputString.substring(shift, shift + 40));
                    shift += 40;
                    transaction.setGasLimit(Long.valueOf(inputString.substring(shift, shift + 16),16));
                    shift += 16;
                    transaction.setFee(Long.valueOf(inputString.substring(shift, shift + 16),16));
                    shift += 16;
                    // DELIMITER2 "0144"
                    shift += 4;
                    transaction.setFunctionNameHash(inputString.substring(shift, shift + 8));
                    shift += 8;
                    transaction.setCallingData(inputString.substring(shift));
                    transaction.setIsDeploy(false);
                } else {
                    //TODO check if next byte is "00" for deploy transactions. Next calculation expect that condition
                    //DELIMITER3 "00"
                    shift += 2;
                    transaction.setContractAddress("");
                    transaction.setGasLimit(Long.valueOf(inputString.substring(shift, shift + 16),16));
                    shift += 16;
                    transaction.setFee(Long.valueOf(inputString.substring(shift, shift + 16),16));
                    shift += 16;
                    //TODO 3 bytes of unknown data Example: "0205C5"
                    transaction.setFunctionNameHash(inputString.substring(shift, shift + 6));
                    shift += 6;
                    transaction.setCallingData(inputString.substring(shift));
                    transaction.setIsDeploy(true);
                }
                return transaction;
            } catch (Exception e) {
                throw new NotValidTransactionException(e);
            }
        }
        return null;
    }

    private boolean isDeployTx(String transaction) {
        Pattern pattern = Pattern.compile(DEPLOY_MARKER + ".*" + DEPLOY_MARKER);
        return pattern.matcher(transaction).find();
    }
}
