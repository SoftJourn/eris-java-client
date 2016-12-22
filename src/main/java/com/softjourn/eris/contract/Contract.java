package com.softjourn.eris.contract;


import com.softjourn.eris.contract.response.Response;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Consumer;

public interface Contract extends Closeable, AutoCloseable {

    /**
     * Call contract function with passed arguments
     *
     * @param function name of function to be called
     * @param args     arguments to pass to called functions(can be zero arguments)
     * @return JSON representation of result of call
     */
    Response call(String function, Object... args) throws IOException;


    String subscribeToUserIn(String address, Consumer<Response> callBack);

    String subscribeToUserOut(String address, Consumer<Response> callBack);

    String subscribeToUserCall(String address, Consumer<Response> callBack);

    void unsubscribe(String subscriptionId);

    String getAddress();
}
