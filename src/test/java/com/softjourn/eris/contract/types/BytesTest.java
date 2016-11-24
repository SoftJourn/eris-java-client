package com.softjourn.eris.contract.types;

import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;


public class BytesTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void formatInput() throws Exception {
        assertEquals("00000000000000000000000000000000000000000000000000000000000000201234567890000000000000000000000000000000000000000000000000000000", new Bytes().formatInput(new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}));
        assertEquals("00000000000000000000000000000000000000000000000000000000000000051234567890000000000000000000000000000000000000000000000000000000", new Bytes().formatInput(new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90}));
        assertEquals("1234567890000000000000000000000000000000000000000000000000000000", new Bytes(32).formatInput(new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}));
        assertEquals("1234567890000000000000000000000000000000000000000000000000000000", new Bytes(16).formatInput(new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}));
    }

    @Test(expected = IllegalArgumentException.class)
    public void formatInput_tooLong() throws Exception {
        new Bytes(16).formatInput(new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
    }

    @Test
    public void formatOutput() throws Exception {
        assertArrayEquals(new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, new Bytes(32).formatOutput("1234567890000000000000000000000000000000000000000000000000000000"));
        assertArrayEquals(new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, new Bytes().formatOutput("00000000000000000000000000000000000000000000000000000000000000201234567890000000000000000000000000000000000000000000000000000000"));
        assertArrayEquals(new byte[]{0x12, 0x34, 0x56, 0x78, (byte) 0x90, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, new Bytes().formatOutput("00000000000000000000000000000000000000000000000000000000000000101234567890000000000000000000000000000000000000000000000000000000"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void formatOutput_wrongInput() throws Exception {
        new Bytes(32).formatOutput("123456u7890000000000000000000000000000000000000000000000000000000");
    }

    @Test
    public void canRepresent() throws Exception {
        assertTrue(new Bytes(32).canRepresent("0000000000000000000000000000000000000000000000000000000000000005"));
        assertTrue(new Bytes(32).canRepresent("1234567890000000000000000000000000000000000000000000000000000000"));
        assertTrue(new Bytes().canRepresent("0000000000000000000000000000000000000000000000000000000000000064"));
        assertFalse(new Bytes(64).canRepresent("00000000000000000000000000000000000000000G000000000000000000005"));
    }

    @Test
    public void toStringTest() throws Exception {
        assertEquals("bytes", new Bytes().toString());
        assertEquals("bytes256", new Bytes(256).toString());
    }

    @Test
    public void isDynamic() throws Exception {
        assertFalse(new Bytes(16).isDynamic());
        assertFalse(new Bytes(32).isDynamic());
        assertTrue(new Bytes().isDynamic());
    }

    @Test
    public void valueClass() throws Exception {
        assertEquals(byte[].class, new Bytes().valueClass());
        assertNotEquals(BigInteger.class, new Bytes().valueClass());
    }

    @Test
    public void isType() throws Exception {
        assertTrue(new Bytes().isType("bytes"));
        assertTrue(new Bytes().isType("bytes32"));
        assertTrue(new Bytes().isType("bytes64"));
        assertTrue(new Bytes().isType("bytes8"));
        assertTrue(new Bytes().isType("bytes256"));
        assertFalse(new Bytes().isType("bytes32g"));
        assertFalse(new Bytes().isType(""));
        assertFalse(new Bytes().isType("bytes32  "));
        assertFalse(new Bytes().isType("  bytes32"));
        assertFalse(new Bytes().isType("address"));
        assertFalse(new Bytes().isType("uint"));
        assertFalse(new Bytes().isType("uint256"));
    }

    @Test
    public void createFromName() throws Exception {
        assertEquals(0, ((Bytes) new Bytes().createFromName("bytes")).length);
        assertEquals(32, ((Bytes) new Bytes().createFromName("bytes32")).length);
        assertEquals(256, ((Bytes) new Bytes().createFromName("bytes256")).length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createFromName_wrong() throws Exception {
        new Bytes().createFromName("address");
    }

}