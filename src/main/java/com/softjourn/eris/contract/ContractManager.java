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
    private final ArgumentsDecoder decoder;

    public ContractManager() {
        mapper = new ObjectMapper();
        contractUnitReader = mapper.readerFor(ContractUnit.class);
        decoder = new ArgumentsDecoder();
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

        ContractBuilder withParameters(Object... args) throws IOException {
            if (solidityByteCode == null)
                throw new RuntimeException("Solidity byte code is not provided. Can't create contract");
            else {
                ContractUnit unit = contractUnits.get(null);
                this.solidityByteCode += decoder.writeArgs(unit, args).toUpperCase();
                return this;
            }
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

        public Contract buildAndDeploy() {
            if (eventHandler == null)
                throw new RuntimeException("EventHandler is not provided. Can't create contract.");
            if (client == null) throw new RuntimeException("RPCClient is not provided. Can't create contract.");
            if (accountData == null) throw new RuntimeException("Account is not provided. Can't create contract.");
            if (solidityByteCode == null)
                throw new RuntimeException("Solidity byte code is not provided. Can't create contract");
            return new ContractImpl(deploy().getResult().getCall_data().getCallee(),
                    client, (Map<String, ContractUnit>) contractUnits.clone(), accountData, eventHandler);
        }

        private DeployResponse deploy() {
            try {
                Map<String, Object> params = Params.transactionalCallParams(accountData.getPrivKey(),
                        "", solidityByteCode);
                RPCRequestEntity entity = ErisRPCRequestEntity.transactionalCallEntity(params);
                String call = client.call(entity);
                return deployParser(call);
            } catch (IOException e) {
                throw new ContractDeploymentException(e);
            }
        }

        private DeployResponse deployParser(String json) throws IOException {
            JsonNode jsonNode = mapper.readTree(json);
            Error error = getError(jsonNode);
            if (error == null) {
                DeployResult deployResult = mapper.treeToValue(jsonNode.get("result"), DeployResult.class);
                return new DeployResponse(jsonNode.get("id").asText(), deployResult, null, jsonNode.get("jsonrpc").asText());
            } else {
                throw new ContractDeploymentException(error.getMessage());
            }
        }

        private Error getError(JsonNode res) throws IOException {
            if (!res.has("error")) throw new ResponseParsingException("Wrong response. Error field is not presented.");
            JsonNode error = res.get("error");
            ObjectReader reader = mapper.readerFor(Error.class);
            return reader.readValue(error);
        }

    }

}
