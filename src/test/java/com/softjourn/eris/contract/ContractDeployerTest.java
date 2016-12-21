package com.softjourn.eris.contract;

import com.softjourn.eris.ErisAccountData;
import com.softjourn.eris.contract.event.EventHandler;
import com.softjourn.eris.contract.types.Address;
import com.softjourn.eris.rpc.HTTPRPCClient;
import com.softjourn.eris.rpc.RPCClient;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContractDeployerTest {

    ContractDeployer deployer = new ContractDeployer();

    ErisAccountData account;

    String chainUrl;

    @Before
    public void setUp() {
        chainUrl = "http://172.17.0.1:1337";
        account = new ErisAccountData() {
            @Override
            public String getAddress() {
                return "64D84C8BF6305C7F396C57ECB3DB18B8F51623F3";
            }

            @Override
            public String getPubKey() {
                return "15DC3839C68226ADC8FECD0FB851F2CF2160B0A7B10B328D8D87CE086834A670";
            }

            @Override
            public String getPrivKey() {
                return "72030410B02662D4489333B051119CF39319899F6BF72A91D0301125256CC5E515DC3839C68226ADC8FECD0FB851F2CF2160B0A7B10B328D8D87CE086834A670";
            }
        };
    }


    @Test
    public void deployTest() throws IOException {
        RPCClient client = new HTTPRPCClient(chainUrl);

        Contract contract = deployer.contractBuilder(new File("src/test/resources/crowdsale-abi"))
                .withSolidityByteCode(new File("src/test/resources/code"))
                .withParameters("0x4BE9C281CFE741BDEFA68C26F4B48FC58D6180A7",
                        BigInteger.valueOf(1000),
                        BigInteger.valueOf(10),
                        true,
                        new ArrayList<String>() {{
                            add("0xAB16E684DEAF5473D0A0C6B4671E796F749704C9");
                        }})
                .withRPCClient(client)
                .withEventHandler(new EventHandler(chainUrl))
                .withCallerAccount(account).build();
        System.out.println(contract.toString());
    }

}
