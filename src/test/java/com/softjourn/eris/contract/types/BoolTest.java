package com.softjourn.eris.contract.types;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class BoolTest {

    Bool bool;

    @Before
    public void setUp() throws Exception {
        bool = new Bool();

    }

    @Test
    public void formatInput() throws Exception {
        assertEquals("0000000000000000000000000000000000000000000000000000000000000001" ,bool.formatInput(true));
        assertEquals("0000000000000000000000000000000000000000000000000000000000000000" ,bool.formatInput(false));
    }

    @Test
    public void formatOutput() throws Exception {
        assertTrue(bool.formatOutput("0000000000000000000000000000000000000000000000000000000000000001"));
        assertFalse(bool.formatOutput("0000000000000000000000000000000000000000000000000000000000000000"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void formatOutputWrongInput() throws Exception {
        bool.formatOutput("00000000000000h000000000000000000001");
    }

    @Test
    public void canRepresent() throws Exception {
        assertTrue(bool.canRepresent("0000000000000000000000000000000000000000000000000000000000000001"));
        assertTrue(bool.canRepresent("0000000000000000000000000000000000000000000000000000000000000000"));
        assertFalse(bool.canRepresent("0000000000000000000000000000000000000000000000000000000000000005"));
        assertFalse(bool.canRepresent(null));
        assertFalse(bool.canRepresent("000000000000000000000000000000000000000000000000000000001"));
        assertFalse(bool.canRepresent(""));
    }

    @Test
    public void toStringTest() throws Exception {
        assertEquals("bool", bool.toString());
    }

    @Test
    public void valueClass() throws Exception {
        assertEquals(Boolean.class, bool.valueClass());
    }

    @Test
    public void isType() throws Exception {
        assertTrue(new Bool().isType("bool"));
        assertFalse(new Bool().isType("address"));
        assertFalse(new Bool().isType("uint"));
        assertFalse(new Bool().isType("uint256"));
    }

    @Test
    public void testIsDynamic() throws Exception {
        assertFalse(bool.isDynamic());
    }
}