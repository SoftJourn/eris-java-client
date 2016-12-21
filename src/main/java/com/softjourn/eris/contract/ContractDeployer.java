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
 * Contract deployer - deploys contract
 * for specified chain url and account
 */
public class ContractDeployer {

    private ObjectMapper mapper;
    private final ObjectReader contractUnitReader;
    private final ArgumentsDecoder decoder;

    public ContractDeployer() {
        mapper = new ObjectMapper();
        contractUnitReader = mapper.readerFor(ContractUnit.class);
        decoder = new ArgumentsDecoder();
    }

    public ContractDeployerBuilder contractBuilder(File contractAbiFile) throws IOException {
        return prepare(readContract(contractAbiFile));
    }

    public ContractDeployerBuilder contractBuilder(String contractAbiString) throws IOException {
        return prepare(contractAbiString);
    }

    private static String readContract(File contractAbiFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(contractAbiFile))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    ContractDeployerBuilder prepare(String abiJson) throws IOException {
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
            return new ContractDeployerBuilder(result);
        } catch (IOException e) {
            throw new IOException("Can't read ABI file due to exception", e);
        }
    }

    public class ContractDeployerBuilder {

        private ErisAccountData accountData;

        private RPCClient client;

        private final HashMap<String, ContractUnit> contractUnits;

        private EventHandler eventHandler;

        private String solidityByteCode;

        ContractDeployerBuilder(HashMap<String, ContractUnit> contractUnits) {
            this.contractUnits = contractUnits;
        }

        ContractDeployer.ContractDeployerBuilder withRPCClient(RPCClient client) {
            this.client = client;
            return this;
        }

        ContractDeployer.ContractDeployerBuilder withSolidityByteCode(String byteCode) {
            this.solidityByteCode = byteCode;
            return this;
        }

        ContractDeployer.ContractDeployerBuilder withSolidityByteCode(File file) throws IOException {
            return withSolidityByteCode(readContract(file));
        }

        ContractDeployer.ContractDeployerBuilder withParameters(Object... args) throws IOException {
            ContractUnit unit = contractUnits.get(null);
            this.solidityByteCode += decoder.writeArgs(unit, args).toUpperCase();
            return this;
        }

        ContractDeployer.ContractDeployerBuilder withCallerAccount(ErisAccountData accountData) {
            this.accountData = accountData;
            return this;
        }

        ContractDeployer.ContractDeployerBuilder withEventHandler(EventHandler eventHandler) {
            this.eventHandler = eventHandler;
            return this;
        }

        Contract build() throws IOException {
            if (eventHandler == null)
                throw new RuntimeException("EventHandler is not provided. Can't create contract.");
            if (client == null) throw new RuntimeException("RPCClient is not provided. Can't create contract.");
            if (accountData == null) throw new RuntimeException("Account is not provided. Can't create contract.");
            if (solidityByteCode == null)
                throw new RuntimeException("Solidity byte code is not provided. Can't create contract");
            DeployResponse response = deploy();
            if (response.getResult() == null) {
                throw new ContractDeploymentException(response.getError().getMessage());
            } else {
                return new ContractImpl(response.getResult().getCall_data().getCallee(), client,
                        (Map<String, ContractUnit>) contractUnits.clone(), accountData, eventHandler);
            }
        }

        private DeployResponse deploy() {
            try {
                Map<String, Object> param = new HashMap<>();
                param.put("priv_key", accountData.getPrivKey());
                param.put("address", "");
                param.put("data", this.solidityByteCode);
                param.put("gas_limit", 10000000);
                param.put("fee", 0);
                RPCRequestEntity entity = ErisRPCRequestEntity.transactionalCallEntity(param);
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
                return new DeployResponse(jsonNode.get("id").asText(), null, error, jsonNode.get("jsonrpc").asText());
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