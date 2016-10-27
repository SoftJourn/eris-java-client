package com.softjourn.eris.contract;


import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jcajce.provider.digest.Keccak;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

public class Util {

    private static final MessageDigest SHA3_DIGEST = new Keccak.Digest256();

    /**
     * Get hexadecimal string representation of Keccak256 hash of passed param
     * @param val value to be hashed
     * @return hashed value
     */
    public static String hash(String val) {
        return sha3(val.getBytes());
    }

    public static String sha3(byte[] value) {
        byte[] hash = SHA3_DIGEST.digest(value);
        return Hex.encodeHexString(hash);
    }

    /**
     * Convert hexadecimal string to byte representation
     * @param value string to be converted
     * @return byte array that represents string
     * @throws NumberFormatException if string is not properly formatted hexadecimal value
     */
    public static byte[] hexStringToBytes(String value) {
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

    public static String encodeInt(int value) {
        return leftPad(BigInteger.valueOf(value).toString(16), 64, '0');
    }
}
