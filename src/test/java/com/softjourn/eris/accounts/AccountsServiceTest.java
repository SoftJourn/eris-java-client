package com.softjourn.eris.accounts;

import com.softjourn.eris.ErisAccountData;
import com.softjourn.eris.rpc.RPCClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountsServiceTest {

    @Mock
    private KeyService keyService;

    @Mock
    private RPCClient rpcClient;

    @Mock
    private ErisAccountData accountData;

    private AccountsService accountsService;

    String url = "url";

    private String sendResponse = "{\"result\":{\"tx_hash\":\"E99944372650DD11D120E793476E54117462184D\",\"creates_contract\":0,\"contract_addr\":\"\"},\"error\":null,\"id\":\"\",\"jsonrpc\":\"2.0\"}";
    private String sendResponseError = "{\"result\": null,\"error\":" + "{\"code\": -32603,\n\"message\": \"Error when transacting: Insuffient gas\"\n},\n,\"id\":\"\",\"jsonrpc\":\"2.0\"}";

    @Before
    public void setUp() throws Exception {
        accountsService = new AccountsService(keyService, "key", rpcClient);

        when(keyService.generateNewKey()).thenReturn(accountData);
        when(rpcClient.call(any())).thenReturn(sendResponse);

    }

    @Test
    public void createAccount() throws Exception {
        ErisAccountData accountData = accountsService.createAccount();

        verify(keyService, times(1)).generateNewKey();

        verify(rpcClient, times(1)).call(any());
    }

    @Test(expected = AccountCreatingException.class)
    public void createAccountError() throws Exception {
        when(rpcClient.call(any())).thenReturn(sendResponseError);

        ErisAccountData accountData = accountsService.createAccount();

        verify(keyService, times(1)).generateNewKey();

        verify(rpcClient, times(1)).call(any());
    }

    @Test(expected = AccountCreatingException.class)
    public void createAccountSendError() throws Exception {
        when(rpcClient.call(any())).thenThrow(new IOException());

        ErisAccountData accountData = accountsService.createAccount();

        verify(keyService, times(1)).generateNewKey();

        verify(rpcClient, times(1)).call(any());
    }



}