package com.softjourn.eris.contract;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class UtilTest {

    @Test
    public void tendermintRIPEDM160Hash() throws Exception {
        assertEquals("D9ADB041D75B842E0CA3D263F99CDC8483759A9A", (Util.tendermintAddressRipeMd160Hash(Util.hexStringToBytes("06E06F2630BD32B13A368EEB8CC954C5D48F43B5F45F229A5CCCEBD539F2ED54"))));
        assertEquals("F536275A97D88151B3D5864B8C534BF739EECC2F", (Util.tendermintAddressRipeMd160Hash(Util.hexStringToBytes("0346DC9B3D4C2B3DC31DBED1E2BB429B55BA60B8C3B8A913A096BB65FD048426"))));
        assertEquals("DC9A1D54D7E29668C3D12C64D4196FD52761AAC5", (Util.tendermintAddressRipeMd160Hash(Util.hexStringToBytes("78B8378CC6FAD2B967E7D12AD79CC9FEB4FB93C57FAA0218A30C70DD3FFE226B"))));
    }

    @Test
    public void leftPadTest() throws Exception {
        assertEquals("00001111", Util.leftPad("1111", 8, '0'));
        assertEquals("00000000", Util.leftPad("", 8, '0'));
        assertEquals("11111111", Util.leftPad("11111111", 8, '0'));
        assertEquals("111111111", Util.leftPad("111111111", 8, '0'));
    }

    @Test
    public void rightPadTest() throws Exception {
        assertEquals("11110000", Util.rightPad("1111", 8, '0'));
        assertEquals("00000000", Util.rightPad("", 8, '0'));
        assertEquals("11111111", Util.rightPad("11111111", 8, '0'));
        assertEquals("111111111", Util.rightPad("111111111", 8, '0'));
    }

    @Test
    public void nomberBytesCountTest() {
        assertEquals(0, Util.getNumberLengthInBytes(0));
        assertEquals(1, Util.getNumberLengthInBytes(1));
        assertEquals(1, Util.getNumberLengthInBytes(255));
        assertEquals(2, Util.getNumberLengthInBytes(256));
        assertEquals(2, Util.getNumberLengthInBytes(511));
    }
}