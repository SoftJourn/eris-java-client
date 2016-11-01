package com.softjourn.eris.rpc;


import java.io.IOException;

/**
 * Interface to make simple Http call with Json payload
 */
public interface RPCClient {

    /**
     * Make call to specified URL with Json payload
     * @param entity entity that will be passed as Json payload
     * @return String representation of response body
     * @throws IOException
     */
    String call(RPCRequestEntity entity) throws IOException;
}
