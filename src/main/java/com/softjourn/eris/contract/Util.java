package com.softjourn.eris.contract;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.jcajce.provider.digest.RIPEMD160;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class Util {

    private static final MessageDigest SHA3_DIGEST = new Keccak.Digest256();

    private static final MessageDigest RIPEDM160 = new RIPEMD160.Digest();

    private static final byte[] ADDRESS_HASH_CUSTOM_PREFIX_BYTES = new byte[]{1, 1, 32};
    private static final byte[] TRANSACTION_HASH_CUSTOM_PREFIX_BYTES = new byte[]{2, 1, 101};



    /**
     * Get hexadecimal string representation of Keccak256 hash of passed param
     * @param val value to be hashed
     * @return hashed value
     */
    public static String hash(String val) {
        return sha3(val.getBytes());
    }

    private static String sha3(byte[] value) {
        byte[] hash = SHA3_DIGEST.digest(value);
        return Hex.encodeHexString(hash);
    }

    public static HashMap<String, ContractUnit> parseAbi(String abi) throws IOException {
        if (abi == null) {
            throw new IllegalArgumentException("ABI can't be null");
        }
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader objectReader = mapper.readerFor(ContractUnit.class);
        HashMap<String, ContractUnit> result = new HashMap<>();
        Iterator<ContractUnit> contractUnitIterator = objectReader.readValues(abi);
        while (contractUnitIterator.hasNext()) {
            ContractUnit contractUnit = contractUnitIterator.next();
            if (contractUnit.getType() == ContractUnitType.constructor ||
                    contractUnit.getType() == ContractUnitType.function ||
                    contractUnit.getType() == ContractUnitType.event)
                result.put(contractUnit.getName(), contractUnit);
        }
        return result;
    }

    /**
     * Get Eris(temdermint) specific Ripedm160 Hash as account address
     * @param value public key value
     * @return addess value
     */
    public static String tendermintAddressRipeMd160Hash(byte[] value) {
        return tendermintRIPEDM160Hash(value, ADDRESS_HASH_CUSTOM_PREFIX_BYTES);
    }

    /**
     * Get Eris(temdermint) specific Ripedm160 Hash as account address
     * @param value public key value
     * @return addess value
     */
    public static String tendermintTransactionV11RipeMd160Hash(byte[] value) {
        return tendermintRIPEDM160Hash(value, TRANSACTION_HASH_CUSTOM_PREFIX_BYTES);
    }

    /**
     * Get Eris(temdermint) specific Ripedm160 Hash as account address
     * @param value public key value
     * @return addess value
     */
    public static String tendermintTransactionV12RipeMd160Hash(byte[] value) {
        byte[] hash = RIPEDM160.digest(value);
        return Hex.encodeHexString(hash);
    }

    /**
     * Get Eris(temdermint) specific Ripedm160 Hash
     * Eris add some 3 custom bytes.
     * For different hashes different bytes
     * @param value public key value
     * @return addess value
     */
     private static String tendermintRIPEDM160Hash(byte[] value, byte[] prefix) {
        byte[] withType = new byte[value.length+3];

        System.arraycopy(prefix, 0, withType, 0, 3);
        System.arraycopy(value, 0, withType, 3, value.length);
        byte[] hash = RIPEDM160.digest(withType);
        return Hex.encodeHexString(hash);
    }

    /**
     * Convert hexadecimal string to byte representation
     * @param value string to be converted
     * @return byte array that represents string
     * @throws NumberFormatException if string is not properly formatted hexadecimal value
     */
    static byte[] hexStringToBytes(String value) {
        return new BigInteger(value, 16).toByteArray();
    }

    /**
     * Fill left side of passed value with passed {@param c}
     * to make it required {@param length}.
     * If value length is already greater or equals returns {@param val} without any changes
     * @param val value to be padded
     * @param length required length
     * @param c chat to fill with
     * @return padded to required length value
     */
    public static String leftPad(String val, int length, char c) {
        int diff = length - val.length();
        if (diff > 0) {
            char[] pad = new char[diff];
            Arrays.fill(pad, c);
            return new String(pad) + val;
        }
        return val;
    }

    /**
     * Fill right side of passed value with passed {@param c}
     * to make it required {@param length}.
     * If value length is already greater or equals returns {@param val} without any changes
     * @param val value to be padded
     * @param length required length
     * @param c chat to fill with
     * @return padded to required length value
     */

    @SuppressWarnings("SameParameterValue")
    public static String rightPad(String val, int length, char c) {
        int diff = length - val.length();
        if (diff > 0) {
            char[] pad = new char[diff];
            Arrays.fill(pad, c);
            return val + new String(pad);
        }
        return val;
    }

    private static String getAbsentString(String val, int length, char c) {
        int diff = length - val.length();
        if (diff > 0) {
            char[] pad = new char[diff];
            Arrays.fill(pad, c);
            return new String(pad);
        }
        return "";
    }

    public static String encodeInt(int value) {
        return leftPad(BigInteger.valueOf(value).toString(16), 64, '0');
    }
}
