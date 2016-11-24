package com.softjourn.eris.contract.types;

import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class ArrayTest {

    private Array<String> addressArray = new Array<>(new Address());
    private Array<BigInteger> uintArray = new Array<>(new Uint());
    private Array<BigInteger> staticUintArray = new Array<BigInteger>(new Uint(), 2);

    @Test
    public void formatInput() throws Exception {
        String expectedAddressArray = "000000000000000000000000000000000000000000000000000000000000000200000000000000000000000022222222222222222222222222222222222222220000000000000000000000001111111111111111111111111111111111111111";
        String actualAddressArray = addressArray.formatInput(Arrays.asList("0000000000000000000000002222222222222222222222222222222222222222", "0000000000000000000000001111111111111111111111111111111111111111"));
        assertEquals(expectedAddressArray, actualAddressArray);

        String expectedUintArray = "00000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000005000000000000000000000000000000000000000000000000000000000000000F";
        String actualUintArray = uintArray.formatInput(Arrays.asList(BigInteger.valueOf(5L), BigInteger.valueOf(15L)));
        assertEquals(expectedUintArray, actualUintArray);

        String expectedStaticUintArray = "0000000000000000000000000000000000000000000000000000000000000005000000000000000000000000000000000000000000000000000000000000000F";
        String actualStaticUintArray = staticUintArray.formatInput(Arrays.asList(BigInteger.valueOf(5L), BigInteger.valueOf(15L)));
        assertEquals(expectedStaticUintArray, actualStaticUintArray);
    }

    @Test
    public void formatOutput() throws Exception {
        String inputAddresses = "000000000000000000000000000000000000000000000000000000000000000200000000000000000000000022222222222222222222222222222222222222220000000000000000000000001111111111111111111111111111111111111111";
        List<String> actualAddressArray = addressArray.formatOutput(inputAddresses);
        assertEquals(Arrays.asList("2222222222222222222222222222222222222222", "1111111111111111111111111111111111111111"), actualAddressArray);

        String inputUints = "00000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000005000000000000000000000000000000000000000000000000000000000000000F";
        List<BigInteger> actualUintArray = uintArray.formatOutput(inputUints);
        assertEquals(Arrays.asList(BigInteger.valueOf(5L), BigInteger.valueOf(15L)), actualUintArray);
    }

    @Test(expected = IllegalArgumentException.class)
    public void formatOutputWrongLength() {
        String inputUints = "00000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000005000000000000000000000000000000000000000000000000000000000000000F";
        uintArray.formatOutput(inputUints);
    }

    @Test
    public void canRepresent() throws Exception {
        assertTrue(addressArray.canRepresent("000000000000000000000000000000000000000000000000000000000000000200000000000000000000000022222222222222222222222222222222222222220000000000000000000000001111111111111111111111111111111111111111"));
        assertFalse(addressArray.canRepresent("000000000000000000000000000000000000000000000000000000000000000400000000000000000000000022222222222222222222222222222222222222220000000000000000000000001111111111111111111111111111111111111111"));
        assertFalse(addressArray.canRepresent("0000000000000000000000000000000000000000000000000000000000000002000000000000000000000000222222222222222222222222222222222222222200000000000000000000000011111111111111111111111111111111111111111111"));

        assertTrue(uintArray.canRepresent("00000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000005000000000000000000000000000000000000000000000000000000000000000F"));
        assertFalse(uintArray.canRepresent("00000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000005000000000000000000000000000000000000000000000000000000000000000G"));
        assertFalse(uintArray.canRepresent("00000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000005000000000000000000000000000000000000000000000000000000000000000F"));
    }

    @Test
    public void isDynamic() throws Exception {
        assertTrue(addressArray.createFromName("address[]").isDynamic());
        assertTrue(addressArray.createFromName("address[5][]").isDynamic());
        assertFalse(addressArray.createFromName("address[5]").isDynamic());
        assertFalse(addressArray.createFromName("address[5][5]").isDynamic());
    }

    @Test
    public void valueClass() throws Exception {
        assertTrue(List.class.isAssignableFrom(uintArray.valueClass()));
        assertTrue(List.class.isAssignableFrom(addressArray.valueClass()));
    }

    @Test
    public void isTypeTest() throws Exception {
        assertTrue(addressArray.isType("address[]"));
        assertTrue(uintArray.isType("uint256[]"));
        assertTrue(uintArray.isType("uint256[2]"));
        assertTrue(uintArray.isType("uint256[2][]"));
        assertFalse(addressArray.isType("uint256[]"));
        assertFalse(uintArray.isType("uint256"));
        assertFalse(addressArray.isType(""));
    }

    @Test
    public void getStaticArrayLengthTest() {
        assertEquals(1, addressArray.createFromName("address[]").getStaticArrayLength());
        assertEquals(1, addressArray.createFromName("address[1]").getStaticArrayLength());
        assertEquals(1, addressArray.createFromName("address[2][]").getStaticArrayLength());
        assertEquals(2, addressArray.createFromName("address[2]").getStaticArrayLength());
        assertEquals(3, addressArray.createFromName("address[2][3]").getStaticArrayLength());
    }
}