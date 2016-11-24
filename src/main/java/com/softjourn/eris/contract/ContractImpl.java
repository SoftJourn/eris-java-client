package com.softjourn.eris.contract;

import com.softjourn.eris.ErisAccountData;
import com.softjourn.eris.contract.event.EventHandler;
import com.softjourn.eris.contract.response.Response;
import com.softjourn.eris.rpc.ErisRPCRequestEntity;
import com.softjourn.eris.rpc.Params;
import com.softjourn.eris.rpc.RPCClient;
import lombok.NonNull;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;


class ContractImpl implements Contract, Cloneable {

    private final String contractAddress;

    private final RPCClient client;

    private final Map<String, ContractUnit> contractUnits;

    private final ErisAccountData accountData;

    private final EventHandler eventHandler;

    private final ArgumentsDecoder decoder;

    ContractImpl(String contractAddress, RPCClient client, @NonNull Map<String, ContractUnit> contractUnits, ErisAccountData accountData, EventHandler eventHandler) {
        this.contractAddress = contractAddress;
        this.client = client;
        this.contractUnits = contractUnits;
        this.accountData = accountData;
        this.eventHandler = eventHandler;
        decoder = new ArgumentsDecoder();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Response<T> call(String function, Object... args) throws IOException {
        Variable[] retVars = contractUnits.get(function).getOutputs();
        ResponseParser parser;
        if (retVars.length == 0) {
            parser = new ResponseParser<>(null);
        } else {
            parser = new ResponseParser<>(retVars[0]);
        }

        String response = client.call(callRPCParams(function, args));

        return parser.parse(response);
    }

    @Override
    public String subscribeToUserIn(String address, Consumer<Response> callBack) {
        return eventHandler.subscribe(constructAccountInEventId(address), mapping(callBack));
    }

    @Override
    public String subscribeToUserOut(String address, Consumer<Response> callBack) {
        return eventHandler.subscribe(constructAccountOutEventId(address), mapping(callBack));
    }

    @Override
    public String subscribeToUserCall(String address, Consumer<Response> callBack) {
        return eventHandler.subscribe(constructAccountCallEventId(address), mapping(callBack));
    }

    @Override
    public void unsubscribe(String subscriptionId) {
        eventHandler.unsubscribe(subscriptionId);
    }

    private Consumer<String> mapping(Consumer<Response> callBack) {
        return s -> callBack.accept(new ResponseParser<>(null).apply(s));
    }

    private String constructAccountInEventId(String accountAddress) {
        return "Acc/" + accountAddress + "/Input";
    }

    private String constructAccountOutEventId(String accountAddress) {
        return "Acc/" + accountAddress + "/Output";
    }

    private String constructAccountCallEventId(String accountAddress) {
        return "Acc/" + accountAddress + "/Call";
    }


    ErisRPCRequestEntity callRPCParams(String contractUnitName, Object... args) {
        if (contractUnits.get(contractUnitName).isConstant()) {
            Map<String, Object> params = Params.constantCallParams(accountData.getAddress(), contractAddress, callRPCData(contractUnitName, args));
            return ErisRPCRequestEntity.constantCallEntity(params);
        } else {
            Map<String, Object> params = Params.transactionalCallParams(accountData.getPrivKey(), contractAddress, callRPCData(contractUnitName, args));
            return ErisRPCRequestEntity.transactionalCallEntity(params);
        }
    }

    String callRPCData(String contractUnitName, Object... args) {
        if (!contractUnits.containsKey(contractUnitName)) {
            throw new RuntimeException("ContractImpl haven't function with name " + contractUnitName);
        }

        ContractUnit unit = contractUnits.get(contractUnitName);

        return (unit.signature() + decoder.writeArgs(unit, args)).toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContractImpl contract = (ContractImpl) o;

        return contractUnits.equals(contract.contractUnits);

    }

    @Override
    public int hashCode() {
        return contractUnits.hashCode();
    }

    @Override
    public void close() throws IOException {
        if (eventHandler != null) eventHandler.close();
        if (client != null) client.close();
    }

    @Override
    public String toString() {
        return "ContractImpl{" +
                "contractUnits=" + contractUnits +
                ", contractAddress='" + contractAddress + '\'' +
                '}';
    }
}
