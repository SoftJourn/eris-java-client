package com.softjourn.eris.accounts;

import com.softjourn.eris.ErisAccountData;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class KeyServiceTest {

    private KeyService keyService = new KeyService();

    @Test
    public void generateNewKeyTest() throws Exception {

        ErisAccountData accountData = keyService.generateNewKey();
        assertNotNull(accountData.getAddress());
        assertNotNull(accountData.getPrivKey());
        assertNotNull(accountData.getPubKey());

        assertEquals(40, accountData.getAddress().length());
        assertEquals(64, accountData.getPubKey().length());
        assertEquals(128, accountData.getPrivKey().length());

        assertTrue(accountData.getPrivKey().endsWith(accountData.getPubKey()));
    }

}