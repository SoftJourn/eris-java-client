package com.softjourn.eris.contract;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.softjourn.eris.ErisAccountData;
import com.softjourn.eris.contract.response.DeployResponse;
import com.softjourn.eris.contract.response.DeployResult;
import com.softjourn.eris.contract.response.Error;
import com.softjourn.eris.contract.response.ResponseParsingException;
import com.softjourn.eris.rpc.RPCClient;
import com.softjourn.eris.rpc.RPCRequestEntity;

import java.io.IOException;

import static com.softjourn.eris.rpc.ErisRPCRequestEntity.transactionalCallEntity;
import static com.softjourn.eris.rpc.Params.transactionalCallParams;

public class ContractDeployer {

    private final ErisAccountData accountData;
    private final RPCClient client;
    private final ObjectMapper mapper;
    private final ArgumentsDecoder decoder;

    /**
     * @param client      - needed to make http request to eris
     * @param accountData - eris's caller account
     */
    public ContractDeployer(RPCClient client, ErisAccountData accountData) {
        this.accountData = accountData;
        this.client = client;
        this.mapper = new ObjectMapper();
        this.decoder = new ArgumentsDecoder();
    }

    /**
     * Deploys new instance of solidity contract
     *
     * @param unit             - structure of contract constructor
     * @param solidityByteCode - contract's compiled code
     * @param args             - solidity's contract constructor parameters
     * @return DeployResponse
     */
    public DeployResponse deploy(ContractUnit unit, String solidityByteCode, Object... args) {
        solidityByteCode += decoder.writeArgs(unit, args).toUpperCase();
        try {
            RPCRequestEntity entity = transactionalCallEntity(transactionalCallParams(accountData.getPrivKey(),
                    "", solidityByteCode));
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
