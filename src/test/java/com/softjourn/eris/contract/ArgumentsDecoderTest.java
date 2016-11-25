package com.softjourn.eris.contract;

import com.softjourn.eris.contract.types.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


public class ArgumentsDecoderTest {

    ArgumentsDecoder decoder = new ArgumentsDecoder();

    ContractUnit function1 = new ContractUnit();

    ContractUnit function2 = new ContractUnit();

    Variable<List<BigInteger>> staticArrayVariabe = new Variable<>("stArr", new Array<>(new Uint(), 2));
    Variable<List<BigInteger>> dynamicArrayVariabe = new Variable<>("dnArr", new Array<>(new Uint()));
    Variable<byte[]> staticBytesVariable = new Variable<>("stBytes", new Bytes(16));
    Variable<byte[]> dynamicBytesVariable = new Variable<>("dnBytes", new Bytes());
    Variable<BigInteger> staticIntVariable = new Variable<>("stInt", new Uint());

    @Before
    public void setUp() throws Exception {
        function1.setConstant(true);
        function1.setAnonymous(false);
        function1.setName("f1");
        function1.setType(ContractUnitType.function);
        function1.setInputs(new Variable[]{staticArrayVariabe, dynamicArrayVariabe, staticBytesVariable, dynamicBytesVariable, staticIntVariable});

        function1.setOutputs(new Variable[]{staticArrayVariabe, dynamicArrayVariabe, staticBytesVariable, dynamicBytesVariable, staticIntVariable});

        function2.setOutputs(new Variable[]{new Variable<>("x", new Address())});

    }

    @Test
    public void readArgs() throws Exception {
        List<Object> expected = Arrays.asList(
                Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(1)),
                Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(2), BigInteger.valueOf(3)),
                new byte[]{(byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
                new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
                BigInteger.valueOf(1)
        );
        String data = "0000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000C0FFFF0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000140000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000030000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000030000000000000000000000000000000000000000000000000000000000000003FFFFFF0000000000000000000000000000000000000000000000000000000000";

        List<Object> result = decoder.readArgs(function1, data);

        for (int i = 0; i < expected.size(); i++) {
            if (expected.get(i) instanceof byte[]) {
                assertArrayEquals((byte[])expected.get(i), (byte[])result.get(i));
            } else {
                assertEquals(expected.get(i), result.get(i));
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void readArgs_Empty() throws Exception {
        String data = "";
        decoder.readArgs(function2, data);
    }

    @Test
    public void writeArgs() throws Exception {
        String expected = "0000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000C0FFFF0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000140000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000030000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000200000000000000000000000000000000000000000000000000000000000000030000000000000000000000000000000000000000000000000000000000000003FFFFFF0000000000000000000000000000000000000000000000000000000000";

        String result = decoder.writeArgs(function1,
                Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(1)),
                Arrays.asList(BigInteger.valueOf(1), BigInteger.valueOf(2), BigInteger.valueOf(3)),
                new byte[]{(byte) 0xFF, (byte) 0xFF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
                new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
                BigInteger.valueOf(1)
        );

        assertEquals(expected, result);
    }

}