package com.softjourn.eris.contract;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.NonNull;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.jcajce.provider.digest.RIPEMD160;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class Util {

    private static final MessageDigest SHA3_DIGEST = new Keccak.Digest256();

    private static final MessageDigest RIPEDM160 = new RIPEMD160.Digest();

    private static final byte TENDERMINT_PUB_KEY_TYPE_ED25519 = 1;
    private static final byte TENDERMINT_PUB_KEY_TYPE_SECP256K1 = 2;

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

    public static HashMap<String, ContractUnit> parseAbi(@NonNull String abi) throws IOException {
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
        return tendermintRIPEDM160Hash(value, preppend(getBytesPrefix(value), TENDERMINT_PUB_KEY_TYPE_ED25519));
    }

    private static byte[] preppend(byte[] data, byte... value) {
        byte[] result = new byte[data.length + value.length];
        System.arraycopy(value, 0, result, 0, value.length);
        System.arraycopy(data, 0, result, value.length, data.length);
        return result;
    }

    /**
     * Get Eris(temdermint) specific Ripedm160 Hash as account address
     * @param value public key value
     * @return addess value
     */
    public static String tendermintTransactionV11RipeMd160Hash(byte[] value) {
        return tendermintRIPEDM160Hash(value, getBytesPrefix(value));
    }

    private static byte[] getBytesPrefix(byte[] value) {
        int length = value.length;
        byte size = getNumberLengthInBytes(length);
        int prefixLength = size + 1;
        byte[] prefix = new byte[prefixLength];
        prefix[0] = size;
        byte[] lengthBytes = longToBytes(length);
        System.arraycopy(lengthBytes, 8 - size, prefix, 1, size);
        return prefix;
    }

    private static byte[] longToBytes(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(value);
        return buffer.array();
    }

    /**
     * @return minimal bytes count to represent given number as unsigned int
     */
    public static byte getNumberLengthInBytes(long number) {
        return (byte) (8 - Long.numberOfLeadingZeros(number) / 8);
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
        byte[] withType = new byte[value.length+prefix.length];

        System.arraycopy(prefix, 0, withType, 0, prefix.length);
        System.arraycopy(value, 0, withType, prefix.length, value.length);
        byte[] hash = RIPEDM160.digest(withType);
        return Hex.encodeHexString(hash).toUpperCase();
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
