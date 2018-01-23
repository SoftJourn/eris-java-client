package com.softjourn.eris.accounts;

import com.softjourn.eris.ErisAccountData;
import com.softjourn.eris.contract.ResponseParser;
import com.softjourn.eris.contract.response.Response;
import com.softjourn.eris.rpc.ErisRPCRequestEntity;
import com.softjourn.eris.rpc.Params;
import com.softjourn.eris.rpc.RPCClient;

import java.io.IOException;

public class AccountsService {

    private static final Integer DEFAULT_AMOUNT_TO_SEND = 1_000_000;

    private KeyService keyService;

    private String userPrivateKey;

    private RPCClient client;

    public AccountsService(KeyService keyService, String userPrivateKey, RPCClient client) {
        this.keyService = keyService;
        this.userPrivateKey = userPrivateKey;
        this.client = client;
    }

    public ErisAccountData createAccount() {
        try {
            ErisAccountData account = keyService.generateNewKey();
            Response response = sendTokens(userPrivateKey, account.getAddress(), DEFAULT_AMOUNT_TO_SEND);
            if (response.getError() != null) throw new AccountCreatingException(response.getError().getMessage());
            setPermissions();
            return account;
        } catch (IOException e) {
            throw new AccountCreatingException(e);
        }

    }

    public Response sendTokens(String userPrivateKey, String address, Integer defaultAmountToSend) throws IOException {

        ResponseParser parser = new ResponseParser(null);

        String response = client.call(ErisRPCRequestEntity.sendCallEntity(Params.sendParams(userPrivateKey, address, defaultAmountToSend)));
        return parser.parse(response);
    }

    private void setPermissions() {
        //TODO Not implemented yet
    }


}
