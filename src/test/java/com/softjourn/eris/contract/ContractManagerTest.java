package com.softjourn.eris.contract;

import com.softjourn.eris.contract.types.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("unchecked")
public class ContractManagerTest {

    private ContractImpl testContract;

    @Before
    public void setUp() throws Exception {
        Type uintType = new Uint(256);
        Type boolType = new Bool();
        Type arrayAddressType = new Array(new Address());
        Type arrayOfArrayAddressType = new Array(new Array(new Address()));
        Type arrayOfArrayFixedAddressType = new Array(new Array(new Address(), 2), 2);



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

        final ContractUnit sendFunction = new ContractUnit();
        sendFunction.setConstant(false);
        sendFunction.setInputs(new Variable[]{
                new Variable("account", new Address()),
                new Variable("amount", uintType)
        });
        sendFunction.setName("send");
        sendFunction.setOutputs(new Variable[0]);
        sendFunction.setType(ContractUnitType.function);

        final ContractUnit distributeFunction = new ContractUnit();
        distributeFunction.setConstant(false);
        distributeFunction.setInputs(new Variable[]{
                new Variable("accounts", arrayAddressType),
                new Variable("amount", uintType)});
        distributeFunction.setName("distribute");
        distributeFunction.setOutputs(new Variable[]{
                new Variable("success", boolType)
        });
        distributeFunction.setType(ContractUnitType.function);

        final ContractUnit distributeFunction2A = new ContractUnit();
        distributeFunction2A.setConstant(false);
        distributeFunction2A.setInputs(new Variable[]{
                new Variable("accounts", arrayOfArrayAddressType),
                new Variable("amount", uintType)});
        distributeFunction2A.setName("distribute");
        distributeFunction2A.setOutputs(new Variable[]{
                new Variable("success", boolType)
        });
        distributeFunction2A.setType(ContractUnitType.function);


        final ContractUnit distributeFunction2AFixed = new ContractUnit();
        distributeFunction2AFixed.setConstant(false);
        distributeFunction2AFixed.setInputs(new Variable[]{
                new Variable("accounts", arrayOfArrayFixedAddressType),
                new Variable("amount", uintType)});
        distributeFunction2AFixed.setName("distribute");
        distributeFunction2AFixed.setOutputs(new Variable[]{
                new Variable("success", boolType)
        });
        distributeFunction2AFixed.setType(ContractUnitType.function);


        Map<String, ContractUnit> contractUnits = new HashMap<String, ContractUnit>() {{
            put("get", getFunction);
            put("set", setFunction);
            put("send", sendFunction);
            put("distribute", distributeFunction);
            put("distribute2a", distributeFunction2A);
            put("distribute2af", distributeFunction2AFixed);
        }};

        testContract = new ContractImpl("", null, contractUnits, null, "", null);

    }

    @Test
    public void testCallRPC() throws Exception {
        String EXPECTED_GET = "6D4CE63C";
        assertEquals(EXPECTED_GET, testContract.callRPCData("get"));
        String EXPECTED_SET = "60FE47B10000000000000000000000000000000000000000000000000000000000000005";
        assertEquals(EXPECTED_SET, testContract.callRPCData("set", BigInteger.valueOf(5L)));
        String EXPECTED_SEND = "D0679D3400000000000000000000000011111111111111111111111111111111111111110000000000000000000000000000000000000000000000000000000000000005";
        assertEquals(EXPECTED_SEND, testContract.callRPCData("send", "1111111111111111111111111111111111111111", BigInteger.valueOf(5L)));
    }



    @Test
    public void testCallRPCWithArray() throws Exception {
        String EXPECTED_DISTRIBUTE = "1826C1190000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000006400000000000000000000000000000000000000000000000000000000000000020000000000000000000000007B7CE331A2ADCA837447D0F25E0399FA8BF5062E0000000000000000000000007427D11D1D717A559DD6C76C73162A592A82DFD1";
        assertEquals(EXPECTED_DISTRIBUTE, testContract.callRPCData("distribute", Arrays.asList("7B7CE331A2ADCA837447D0F25E0399FA8BF5062E", "7427D11D1D717A559DD6C76C73162A592A82DFD1"), BigInteger.valueOf(100L)));
    }


}