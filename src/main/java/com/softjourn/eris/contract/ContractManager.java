package com.softjourn.eris.contract;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.softjourn.eris.ErisAccountData;
import com.softjourn.eris.contract.event.EventHandler;
import com.softjourn.eris.contract.response.DeployResponse;
import com.softjourn.eris.contract.response.DeployResult;
import com.softjourn.eris.contract.response.Error;
import com.softjourn.eris.contract.response.ResponseParsingException;
import com.softjourn.eris.rpc.ErisRPCRequestEntity;
import com.softjourn.eris.rpc.Params;
import com.softjourn.eris.rpc.RPCClient;
import com.softjourn.eris.rpc.RPCRequestEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contract manager to create or deploy contract objects
 * for specified chain url and account
 */
public class ContractManager {

    ObjectMapper mapper;
    private final ObjectReader contractUnitReader;


    public ContractManager() {
        mapper = new ObjectMapper();
        contractUnitReader = mapper.readerFor(ContractUnit.class);
    }

    public ContractBuilder contractBuilder(File contractAbiFile) throws IOException {
        return parseContract(readContract(contractAbiFile));
    }

    public ContractBuilder contractBuilder(String contractAbiString) throws IOException {
        return parseContract(contractAbiString);
    }

    private static String readContract(File contractAbiFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(contractAbiFile))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    ContractBuilder parseContract(String abiJson) throws IOException {
        try {
            HashMap<String, ContractUnit> result = new HashMap<>();
            Iterator<ContractUnit> contractUnitIterator = contractUnitReader.readValues(abiJson);
            while (contractUnitIterator.hasNext()) {
                ContractUnit contractUnit = contractUnitIterator.next();
                if (contractUnit.getType() == ContractUnitType.constructor ||
                        contractUnit.getType() == ContractUnitType.function ||
                        contractUnit.getType() == ContractUnitType.event)
                    result.put(contractUnit.getName(), contractUnit);
            }
            return new ContractBuilder(result);
        } catch (IOException e) {
            throw new IOException("Can't read ABI file due to exception", e);
        }
    }

    public class ContractBuilder {

        private ErisAccountData accountData;

        private String contractAddress;

        private RPCClient client;

        private final HashMap<String, ContractUnit> contractUnits;

        private EventHandler eventHandler;

        private String solidityByteCode;

        ContractBuilder(HashMap<String, ContractUnit> contractUnits) {
            this.contractUnits = contractUnits;
        }

        public ContractBuilder withContractAddress(String contractAddress) {
            this.contractAddress = contractAddress;
            return this;
        }

        public ContractBuilder withRPCClient(RPCClient client) {
            this.client = client;
            return this;
        }

        public ContractBuilder withCallerAccount(ErisAccountData accountData) {
            this.accountData = accountData;
            return this;
        }

        public ContractBuilder withEventHandler(EventHandler eventHandler) {
            this.eventHandler = eventHandler;
            return this;
        }

        ContractBuilder withSolidityByteCode(String byteCode) {
            this.solidityByteCode = byteCode;
            return this;
        }

        ContractBuilder withSolidityByteCode(File file) throws IOException {
            return withSolidityByteCode(readContract(file));
        }

        @SuppressWarnings("unchecked")
        public Contract build() {
            if (eventHandler == null)
                throw new RuntimeException("EventHandler is not provided. Can't create contract.");
            if (client == null) throw new RuntimeException("RPCClient is not provided. Can't create contract.");
            if (accountData == null) throw new RuntimeException("Account is not provided. Can't create contract.");
            if (contractAddress == null)
                throw new RuntimeException("Contract address is not provided. Can't create contract.");
            return new ContractImpl(contractAddress, client, (Map<String, ContractUnit>) contractUnits.clone(), accountData, eventHandler);
        }

        public Contract buildAndDeploy(Object... args) {
            if (eventHandler == null)
                throw new RuntimeException("EventHandler is not provided. Can't create contract.");
            if (client == null) throw new RuntimeException("RPCClient is not provided. Can't create contract.");
            if (accountData == null) throw new RuntimeException("Account is not provided. Can't create contract.");
            if (solidityByteCode == null)
                throw new RuntimeException("Solidity byte code is not provided. Can't create contract");
            ContractDeployer deployer = new ContractDeployer(client, accountData);
            DeployResponse response = deployer.deploy(contractUnits.get(null), solidityByteCode, args);
            return new ContractImpl(response.getResult().getCall_data().getCallee(),
                    client, (Map<String, ContractUnit>) contractUnits.clone(), accountData, eventHandler);
        }

    }

}
