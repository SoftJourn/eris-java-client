package com.softjourn.eris.contract.types;

import com.softjourn.eris.contract.Util;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;

public class Bytes extends Type<byte[]> {

    public final int length;

    public Bytes() {
        length = 0;
    }

    public Bytes(int length) {
        this.length = length;
    }

    @Override
    public String formatInput(byte[] value) {
        if (length != 0 && value.length != length) {
            throw new IllegalArgumentException("Type bytes" + length + " can't represent " + value.length + " bytes length array.");
        }
        int outLength = value.length / 32 + value.length % 32 > 0 ? 32 : 0;
        String result = Util.rightPad(new String(Hex.encodeHex(value)), outLength * 2, '0');
        if (length == 0) {
            result = Util.encodeInt(value.length) + result;
        }
        return result;
    }

    @Override
    public byte[] formatOutput(String value) {
        try {
            int resultLength = length == 0 ? getDynamicLength(value) : length;
            int start = length == 0 ? 64 : 0;
            int end = start + resultLength * 2;
            return Hex.decodeHex(value.substring(start, end).toCharArray());
        } catch (DecoderException e) {
            throw new IllegalArgumentException("Value \"" + value + "\' contains non hex characters and can't be represented as byte array.");
        }

    }

    private int getDynamicLength(String value) {
        return new BigInteger(value.substring(0, 64), 16).intValue();
    }

    @Override
    public boolean canRepresent(String value) {
        return (value.length() == length * 2 || length == 0) && value.matches("[0-9a-fA-F]+");
    }

    @Override
    public String toString() {
        return "bytes" + (length > 0 ? length : "");
    }

    @Override
    public boolean isDynamic() {
        return length == 0;
    }

    @Override
    public Class<byte[]> valueClass() {
        return byte[].class;
    }

    @Override
    public boolean isType(String name) {
        return name.matches("^bytes[0-9]*$");
    }

    @Override
    public Type<byte[]> createFromName(String name) {
        if (!isType(name)) throw new IllegalArgumentException("Can't create Bytes type for name \"" + name + "\"");
        return new Bytes(getLength(name));
    }

}
