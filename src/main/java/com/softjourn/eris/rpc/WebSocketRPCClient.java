package com.softjourn.eris.rpc;

import com.neovisionaries.ws.client.*;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSocketRPCClient implements RPCClient, AutoCloseable {

    private static final String ID_RESPONSE_PATTERN = "^.*id\":\\s?\"([0-9a-fA-F]+)\".*$";

    private static final String DEFAULT_SOCKET_ENDPOINT = "/socketrpc";

    private static final int DEFAULT_TIMEOUT = 15000;

    private static final Random random = new Random();

    private Map<String, String> responsesMap = new ConcurrentHashMap<>();

    private Map<String, CountDownLatch> locks = new ConcurrentHashMap<>();

    private WebSocket socket;

    private final long timeoutMillis;

    private WebSocketListener listener;

    public WebSocketRPCClient(String host) {
        this(host, DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public WebSocketRPCClient(String host, int timeout, TimeUnit timeUnit) {
        timeoutMillis = timeUnit.toMillis(timeout);
        try {

            listener = new Listener();
            socket = new WebSocketFactory().createSocket(host + DEFAULT_SOCKET_ENDPOINT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String call(RPCRequestEntity entity) throws IOException {
        try {
            if (! socket.isOpen()) {
                openConnection();
            }
            String id = genId();
            RPCRequestEntity requestEntity = entity.setId(id);

            CountDownLatch lock = new CountDownLatch(1);

            locks.put(id, lock);
            socket.sendText(requestEntity.toString());

            lock.await(timeoutMillis, TimeUnit.MILLISECONDS);

            String response = responsesMap.get(id);
            if (response == null) throw new IOException("Eris server haven't provided response.");
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    private class Listener extends WebSocketAdapter {

        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            handleResponse(text);
        }

    }

    private void handleResponse(String response) {
        String id = getId(response);
        CountDownLatch lock = locks.remove(id);
        ;
        if (lock != null) {
            responsesMap.put(id, response);
            lock.countDown();
        }
    }

    private String getId(String response) {
        Pattern subscriptionIdPattern = Pattern.compile(ID_RESPONSE_PATTERN);
        Matcher matcher = subscriptionIdPattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private synchronized void openConnection() {
        try {
            if (! socket.isOpen()) {
                socket.connect();
                socket.addListener(listener);
            }
        } catch (WebSocketException wse) {
            throw new RuntimeException("Can't establish connection with Eris server via WebSocket.", wse);
        }

    }

    String genId() {
        byte[] id = new byte[32];
        random.nextBytes(id);
        return Hex.encodeHexString(id);
    }

    @Override
    public synchronized void close() {
        if (socket.isOpen()) socket.disconnect(WebSocketCloseCode.NORMAL, null, 100);
        while (socket.isOpen()) {
            System.out.println("Closing...");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
