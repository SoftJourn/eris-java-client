package com.softjourn.eris.contract;

import com.softjourn.eris.ErisAccountData;
import com.softjourn.eris.contract.event.EventHandler;
import com.softjourn.eris.contract.types.Address;
import com.softjourn.eris.contract.types.Bool;
import com.softjourn.eris.contract.types.Uint;
import com.softjourn.eris.rpc.ErisRPCRequestEntity;
import com.softjourn.eris.rpc.RPCClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContractTest {

    @Mock
    EventHandler eventHandler;

    @Mock
    RPCClient rpcClient;

    Contract contract;

    Map<String, ContractUnit> contractUnits;

    @Mock
    ErisAccountData accountData;

    private static final String CONTRACT_ADDRESS = "contactAddress";
    private static final String CHAIN_URL = "http://chain.url";

    private static final String INP_EVENT_ID_PATTERN = "Acc/[0-9A-Za-z]{40}/Input";
    private static final String INP_EVENT_ID = "INPUT";

    private static final String OUT_EVENT_ID_PATTERN = "Acc/[0-9A-Za-z]{40}/Output";
    private static final String OUT_EVENT_ID = "OUTPUT";

    private static final String CALL_EVENT_ID_PATTERN = "Acc/[0-9A-Za-z]{40}/Call";
    private static final String CALL_EVENT_ID = "CALL";

    private final String VALUE_RESPONSE_STRING = "{\n" +
            "    \"result\": {\n" +
            "      \"return\": \"0000000000000000000000000000000000000000000000000000000000000064\",\n" +
            "      \"gas_used\": 0\n" +
            "    },\n" +
            "    \"error\": null,\n" +
            "    \"id\": \"\",\n" +
            "    \"jsonrpc\": \"2.0\"\n" +
            "  }";

    private final String TX_RESPONSE_STRING = "{\n" +
            "    \"result\": [\n" +
            "      4,\n" +
            "      {\n" +
            "        \"call_data\": {\n" +
            "          \"caller\": \"1ADA404B3EEDD5CC971475489A17BAACB9BA5D68\",\n" +
            "          \"callee\": \"628FF6F4BB9C68A8BF9C2BBE04242C4B7FAE6A19\",\n" +
            "          \"data\": \"D0679D340000000000000000000000007EA0B39261842504023A40858099700E061563CE0000000000000000000000000000000000000000000000000000000000000001\",\n" +
            "          \"value\": 1,\n" +
            "          \"gas\": 9701\n" +
            "        },\n" +
            "        \"origin\": \"1ADA404B3EEDD5CC971475489A17BAACB9BA5D68\",\n" +
            "        \"tx_id\": \"8993486AB880DB2144A58989B4E3D72F9656246D\",\n" +
            "        \"return\": \"0000000000000000000000000000000000000000000000000000000000000001\",\n" +
            "        \"exception\": \"\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"error\": null,\n" +
            "    \"id\": \"\",\n" +
            "    \"jsonrpc\": \"2.0\"\n" +
            "  }";

    @Before
    public void setUp() throws Exception {

        ContractUnit query = new ContractUnit();
        query.setConstant(true);
        query.setAnonymous(false);
        query.setInputs(new Variable[0]);
        query.setOutputs(new Variable[]{new Variable("balance", new Uint())});

        ContractUnit tx = new ContractUnit();
        tx.setConstant(false);
        tx.setAnonymous(false);
        tx.setInputs(new Variable[]{new Variable("address", new Address()), new Variable("balance", new Uint())});
        tx.setOutputs(new Variable[]{new Variable("result", new Bool())});


        contractUnits = new HashMap<String, ContractUnit>(){{
            put("query", query);
            put("tx", tx);
        }};

        when(eventHandler.subscribe(matches(INP_EVENT_ID_PATTERN), any())).then(i -> INP_EVENT_ID);
        when(eventHandler.subscribe(matches(OUT_EVENT_ID_PATTERN), any())).then(i -> OUT_EVENT_ID);
        when(eventHandler.subscribe(matches(CALL_EVENT_ID_PATTERN), any())).then(i -> CALL_EVENT_ID);

        when(rpcClient.call(eq(CHAIN_URL), any())).then(i -> {
            ErisRPCRequestEntity entity = (ErisRPCRequestEntity) i.getArguments()[1];
            return entity.getMethod().equals("erisdb.call") ? VALUE_RESPONSE_STRING : TX_RESPONSE_STRING;
        });

        when(accountData.getAddress()).thenReturn("address");
        when(accountData.getPrivKey()).thenReturn("privKey");
        when(accountData.getPubKey()).thenReturn("pubKey");

        contract = new ContractImpl(CONTRACT_ADDRESS, rpcClient, contractUnits, accountData, CHAIN_URL, eventHandler);

    }

    @Test
    public void callQuery() throws Exception {
        assertEquals(BigInteger.valueOf(100L), contract.call("query").getReturnValue().getVal());
        verify(accountData, never()).getPrivKey();
    }

    @Test
    public void callTx() throws Exception {
        assertEquals(true, contract.call("tx", "5DCFF4E2FAE97CDB8DB921386B97A2C16CB2E159", BigInteger.valueOf(100L)).getReturnValue().getVal());
        verify(accountData, atLeastOnce()).getPrivKey();
    }

    @Test(expected = IllegalArgumentException.class)
    public void callTxWithWrongArgsCount() throws Exception {
        assertEquals(true, contract.call("tx", "5DCFF4E2FAE97CDB8DB921386B97A2C16CB2E159").getReturnValue().getVal());
        verify(accountData, atLeastOnce()).getPrivKey();
    }

    @Test(expected = IllegalArgumentException.class)
    public void callTxWithWrongArgsType() throws Exception {
        assertEquals(true, contract.call("tx", "5DCFF4E2FAE97CDB8DB921386B97A2C16CB2E159", "100").getReturnValue().getVal());
        verify(accountData, atLeastOnce()).getPrivKey();
    }

    @Test
    public void subscribeToUserIn() throws Exception {
        assertEquals(INP_EVENT_ID, contract.subscribeToUserIn("5DCFF4E2FAE97CDB8DB921386B97A2C16CB2E159", i -> {}));
    }

    @Test
    public void subscribeToUserOut() throws Exception {
        assertEquals(OUT_EVENT_ID, contract.subscribeToUserOut("5DCFF4E2FAE97CDB8DB921386B97A2C16CB2E159", i -> {}));
    }

    @Test
    public void subscribeToUserCall() throws Exception {
        assertEquals(CALL_EVENT_ID, contract.subscribeToUserCall("5DCFF4E2FAE97CDB8DB921386B97A2C16CB2E159", i -> {}));
    }

}