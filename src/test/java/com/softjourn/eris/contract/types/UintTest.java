package com.softjourn.eris.contract.types;

import junit.framework.TestCase;

import java.math.BigInteger;

public class UintTest extends TestCase {

    Uint uint = new Uint();

    public void testIsType() throws Exception {
        assertTrue(new Uint().isType("uint"));
        assertTrue(new Uint().isType("uint256"));
        assertTrue(new Uint().isType("uint32"));
        assertFalse(new Uint().isType("int"));
        assertFalse(new Uint().isType("address"));
    }

    public void testToString() throws Exception {
        assertEquals("uint", uint.toString());
        assertEquals("uint256", new Uint(256).toString());
    }

    public void testFormatInput() throws Exception {

        assertEquals("0000000000000000000000000000000000000000000000000000000000000005", uint.formatInput(BigInteger.valueOf(5L)));
        assertEquals("000000000000000000000000000000000000000000000000000000000000000A", uint.formatInput(BigInteger.valueOf(10L)));
        assertEquals("0000000000000000000000000000000000000000000000000000000000000100", uint.formatInput(BigInteger.valueOf(256L)));
    }

    public void testFormatOutput() throws Exception {
        assertEquals(BigInteger.valueOf(5L), uint.formatOutput("0000000000000000000000000000000000000000000000000000000000000005"));
        assertEquals(BigInteger.valueOf(10L), uint.formatOutput("000000000000000000000000000000000000000000000000000000000000000A"));
        assertEquals(BigInteger.valueOf(256L), uint.formatOutput("0000000000000000000000000000000000000000000000000000000000000100"));
    }

    public void testCanRepresent() throws Exception {
        assertTrue(uint.canRepresent("0000000000000000000000000000000000000000000000000000000000000005"));
        assertTrue(uint.canRepresent("0000000000000000000000000000000000000000000000000000000000000100"));
        assertTrue(uint.canRepresent("0000000000000000000000000000000000000000000000000000000000000064"));
        assertFalse(uint.canRepresent("00000000000000000000000000000000000000000G000000000000000000005"));
    }

    public void testIsDynamic() throws Exception {
        assertFalse(uint.isDynamic());
    }
}