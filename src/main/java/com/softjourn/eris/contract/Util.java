package com.softjourn.eris.contract;


import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.jcajce.provider.digest.RIPEMD160;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;

public class Util {

    private static final MessageDigest SHA3_DIGEST = new Keccak.Digest256();

    private static final MessageDigest RIPEDM160 = new RIPEMD160.Digest();



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
     * Get Eris(temdermint) specific Ripedm160 Hash as account address
     * @param value public key value
     * @return addess value
     */
    public static String tendermintRIPEDM160Hash(byte[] value) {
        byte[] withType = new byte[value.length+3];
        //Some strange custom values
        withType[0] = 1;
        withType[1] = 1;
        withType[2] = 32;

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

    /**
     * Fill right side of passed value with passed {@param c}
     * to make it required {@param length}.
     * If value length is already greater or equals returns {@param val} without any changes
     * @param val value to be padded
     * @param length required length
     * @param c chat to fill with
     * @return padded to required length value
     */
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
