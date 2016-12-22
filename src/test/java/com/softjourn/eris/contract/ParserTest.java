package com.softjourn.eris.contract;

import com.softjourn.eris.ErisAccountData;
import com.softjourn.eris.contract.event.EventHandler;
import com.softjourn.eris.contract.types.*;
import com.softjourn.eris.rpc.RPCClient;
import com.softjourn.eris.rpc.RPCRequestEntity;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ParserTest {

    private ContractImpl testContract;
    private ContractImpl testContract1;
    private ContractImpl arrayTestContract;

    EventHandler eventHandler;
    ErisAccountData account;

    @Before
    public void setUp() throws Exception {

        eventHandler = mock(EventHandler.class);
        account = mock(ErisAccountData.class);

        Type uintType = new Uint(256);
        Type boolType = new Bool();
        Type arrayUintType = new Array(uintType);


        final ContractUnit getFunction = new ContractUnit();
        getFunction.setConstant(true);
        getFunction.setInputs(new Variable[0]);
        getFunction.setName("get");
        getFunction.setOutputs(new Variable[]{new Variable("retVal", uintType)});
        getFunction.setType(ContractUnitType.function);

        final ContractUnit setFunction = new ContractUnit();
        setFunction.setConstant(false);
        setFunction.setInputs(new Variable[]{new Variable("x", uintType)});
        setFunction.setName("set");
        setFunction.setOutputs(new Variable[0]);
        setFunction.setType(ContractUnitType.function);

        final ContractUnit distributeFunction = new ContractUnit();
        distributeFunction.setConstant(false);
        distributeFunction.setInputs(new Variable[]{new Variable("accounts", arrayUintType), new Variable("amount", uintType)});
        distributeFunction.setName("distribute");
        distributeFunction.setOutputs(new Variable[]{new Variable("success", boolType)});
        distributeFunction.setType(ContractUnitType.function);

        Map<String, ContractUnit> contractUnits = new HashMap<String, ContractUnit>() {{
            put("get", getFunction);
            put("set", setFunction);
        }};

        final ContractUnit constructor = new ContractUnit();
        constructor.setType(ContractUnitType.constructor);
        constructor.setInputs(new Variable[]{});

        final ContractUnit queryBalance = new ContractUnit();
        queryBalance.setName("queryBalance");
        queryBalance.setConstant(true);
        queryBalance.setType(ContractUnitType.function);
        queryBalance.setInputs(new Variable[]{new Variable("addr", new Address())});
        queryBalance.setOutputs(new Variable[]{new Variable("balance", uintType)});

        final ContractUnit mint = new ContractUnit();
        mint.setName("mint");
        mint.setType(ContractUnitType.function);
        mint.setConstant(false);
        mint.setInputs(new Variable[]{
                new Variable("owner", new Address()),
                new Variable("amount", uintType)
        });
        mint.setOutputs(new Variable[0]);

        final ContractUnit send = new ContractUnit();
        send.setName("send");
        send.setType(ContractUnitType.function);
        send.setConstant(false);
        send.setInputs(new Variable[]{
                new Variable("receiver", new Address()),
                new Variable("amount", uintType)
        });
        send.setOutputs(new Variable[0]);

        final ContractUnit sendEvent = new ContractUnit();
        sendEvent.setName("Send");
        sendEvent.setType(ContractUnitType.event);
        sendEvent.setAnonymous(false);
        sendEvent.setInputs(new Variable[]{
                new Variable("from", new Address()),
                new Variable("to", new Address()),
                new Variable("value", uintType)
        });

        Map<String, ContractUnit> testUnits1 = new HashMap<String, ContractUnit>() {{
            put(null, constructor);
            put("queryBalance", queryBalance);
            put("mint", mint);
            put("send", send);
            put("Send", sendEvent);
        }};


        testContract1 = new ContractImpl("", null, testUnits1, null, null);

        testContract = new ContractImpl("", null, contractUnits, null, null);

        arrayTestContract = new ContractImpl("", null, Collections.singletonMap("distribute", distributeFunction), null, null);

    }


    @Test
    public void testParse() throws Exception {
        String TEST_DATA = "[\n" +
                "  {\n" +
                "    \"constant\": false,\n" +
                "    \"inputs\": [\n" +
                "      {\n" +
                "        \"name\": \"x\",\n" +
                "        \"type\": \"uint256\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"name\": \"set\",\n" +
                "    \"outputs\": [],\n" +
                "    \"type\": \"function\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"constant\": true,\n" +
                "    \"inputs\": [],\n" +
                "    \"name\": \"get\",\n" +
                "    \"outputs\": [\n" +
                "      {\n" +
                "        \"name\": \"retVal\",\n" +
                "        \"type\": \"uint256\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"type\": \"function\"\n" +
                "  }\n" +
                "]";
        assertEquals(testContract, new ContractManager().parseContract(TEST_DATA)
                .withEventHandler(eventHandler)
                .withRPCClient(new RPCClient() {
                    @Override
                    public String call(RPCRequestEntity entity) throws IOException {
                        return null;
                    }

                    @Override
                    public void close() {

                    }
                })
                .withCallerAccount(account)
                .withContractAddress("")
                .build());
        String TEST_DATA_1 = "[\n" +
                "  {\n" +
                "    \"constant\": true,\n" +
                "    \"inputs\": [\n" +
                "      {\n" +
                "        \"name\": \"addr\",\n" +
                "        \"type\": \"address\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"name\": \"queryBalance\",\n" +
                "    \"outputs\": [\n" +
                "      {\n" +
                "        \"name\": \"balance\",\n" +
                "        \"type\": \"uint256\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"type\": \"function\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"constant\": false,\n" +
                "    \"inputs\": [\n" +
                "      {\n" +
                "        \"name\": \"owner\",\n" +
                "        \"type\": \"address\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"amount\",\n" +
                "        \"type\": \"uint256\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"name\": \"mint\",\n" +
                "    \"outputs\": [],\n" +
                "    \"type\": \"function\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"constant\": false,\n" +
                "    \"inputs\": [\n" +
                "      {\n" +
                "        \"name\": \"receiver\",\n" +
                "        \"type\": \"address\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"amount\",\n" +
                "        \"type\": \"uint256\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"name\": \"send\",\n" +
                "    \"outputs\": [],\n" +
                "    \"type\": \"function\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"inputs\": [],\n" +
                "    \"type\": \"constructor\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"anonymous\": false,\n" +
                "    \"inputs\": [\n" +
                "      {\n" +
                "        \"indexed\": false,\n" +
                "        \"name\": \"from\",\n" +
                "        \"type\": \"address\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"indexed\": false,\n" +
                "        \"name\": \"to\",\n" +
                "        \"type\": \"address\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"indexed\": false,\n" +
                "        \"name\": \"value\",\n" +
                "        \"type\": \"uint256\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"name\": \"Send\",\n" +
                "    \"type\": \"event\"\n" +
                "  }\n" +
                "]";
        assertEquals(testContract1, new ContractManager().parseContract(TEST_DATA_1)
                .withEventHandler(eventHandler)
                .withContractAddress("")
                .withCallerAccount(account)
                .withRPCClient(new RPCClient() {
                    @Override
                    public String call(RPCRequestEntity entity) throws IOException {
                        return null;
                    }

                    @Override
                    public void close() {

                    }
                })
                .build());
    }

    @Test
    public void testParseWithArray() throws Exception {
        String TEST_DATA = "[\n" +
                "  {\n" +
                "    \"constant\": false,\n" +
                "    \"inputs\": [\n" +
                "      {\n" +
                "        \"name\": \"accounts\",\n" +
                "        \"type\": \"uint256[]\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"amount\",\n" +
                "        \"type\": \"uint256\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"name\": \"distribute\",\n" +
                "    \"outputs\": [\n" +
                "      {\n" +
                "        \"name\": \"success\",\n" +
                "        \"type\": \"bool\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"type\": \"function\"\n" +
                "  }\n" +
                "]";
        assertEquals(arrayTestContract, new ContractManager().parseContract(TEST_DATA)
                .withEventHandler(eventHandler)
                .withCallerAccount(account)
                .withRPCClient(new RPCClient() {
                    @Override
                    public String call(RPCRequestEntity entity) throws IOException {
                        return null;
                    }

                    @Override
                    public void close() {

                    }
                })
                .withContractAddress("")
                .build());
    }

}