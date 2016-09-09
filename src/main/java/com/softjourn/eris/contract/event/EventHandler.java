package com.softjourn.eris.contract.event;


import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.softjourn.eris.rpc.ErisRPCRequestEntity;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EventHandler implements AutoCloseable {

    private static final Random random = new Random();

    private static final int DEFAULT_TIMEOUT = 3000;

    private static final String SUBSCRIBE_METHOD = "erisdb.eventSubscribe";
    private static final String UNSUBSCRIBE_METHOD = "erisdb.eventUnsubscribe";

    private static final String DEFAULT_SOCKET_ENDPOINT = "/socketrpc";

    private static final String SUBSCRIPTION_ID_RESPONSE_PATTERN = "^.*sub_id\":\\s?\"([0-9a-fA-F]+)\".*$";
    private static final String ID_RESPONSE_PATTERN = "^.*id\":\\s?\"([0-9a-fA-F]+)\".*$";

    private Listener listener;

    private WebSocket socket;

    private Map<String, Consumer<String>> handlersMap = new ConcurrentHashMap<>();

    private Map<String, String> subscriptionsMap = new ConcurrentHashMap<>();

    private Map<String, CountDownLatch> locks = new ConcurrentHashMap<>();

    public EventHandler(String url) {
        this(url, new WebSocketFactory());
    }

    public EventHandler(String url, WebSocketFactory factory) {
        try {
            listener = new Listener();
            socket = factory.createSocket(url + DEFAULT_SOCKET_ENDPOINT);
            socket.connect();
            socket.addListener(listener);
        } catch (WebSocketException | IOException e) {
            throw new EventSubscriptionException("Can't connect via websockets to " + url, e);
        }
    }

    @Override
    public void close() {
        socket.disconnect();
    }

    public String subscribe(String eventId, Consumer<String> callBack) {
        return subscribe(eventId, callBack, DEFAULT_TIMEOUT);
    }

    public String subscribe(String eventId, Consumer<String> callBack, int timeoutMilis) {
        try {
            String id = genId();
            CountDownLatch lock = new CountDownLatch(1);
            locks.put(id, lock);
            handlersMap.put(id, this::handleSubscriptionResponse);
            socket.sendText(createSubscribeRequest(eventId, id));

            lock.await(timeoutMilis, TimeUnit.MILLISECONDS);

            String subscriptionId = subscriptionsMap.get(id);
            if (subscriptionId == null) {
                locks.remove(id);
                socket.disconnect();
                throw new EventSubscriptionException("Timed out waiting for server response.");
            }
            handlersMap.put(subscriptionId, callBack);

            return subscriptionId;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleSubscriptionResponse(String response) {
        if (isThisSubscriptionId(response)) {
            String id = getId(response);
            String subscriptionId = getSubscriptionId(response);
            CountDownLatch lock = locks.get(id);
            subscriptionsMap.put(id, subscriptionId);
            lock.countDown();
            locks.remove(id);
            handlersMap.remove(id);
        }
    }

    public void unsubscribe(String subscriptionId) {
        socket.sendText(createUnsubscribeRequest(subscriptionId));
        handlersMap.remove(subscriptionId);

    }

    private String createUnsubscribeRequest(String subscriptionId) {
        Map<String, Object> param = Collections.singletonMap("sub_id", subscriptionId);
        return new ErisRPCRequestEntity(param, UNSUBSCRIBE_METHOD).toString();
    }

    private String createSubscribeRequest(String eventId, String uniqueId) {
        Map<String, Object> param = Collections.singletonMap("event_id", eventId);
        return new ErisRPCRequestEntity(param, SUBSCRIBE_METHOD, uniqueId).toString();
    }

    String genId() {
        byte[] id = new byte[32];
        random.nextBytes(id);
        return new BigInteger(id).toString(16).replaceAll("-", "");
    }

    private class Listener extends WebSocketAdapter {

        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            handle(text);
        }

        private void handle(String text) {
            String subscriptionId = getId(text);
            if (subscriptionId == null) throw new RuntimeException("Wrong response, missing id.");
            Consumer<String> consumer = handlersMap.get(subscriptionId);
            if (consumer == null) {
                unsubscribe(subscriptionId);
            } else {
                consumer.accept(text);
            }

        }
    }

    String getId(String response) {
        Pattern subscriptionIdPattern = Pattern.compile(ID_RESPONSE_PATTERN);
        Matcher matcher = subscriptionIdPattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    String getSubscriptionId(String response) {
        Pattern subscriptionIdPattern = Pattern.compile(SUBSCRIPTION_ID_RESPONSE_PATTERN);
        Matcher matcher = subscriptionIdPattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    boolean isThisSubscriptionId(String response) {
        return response.matches(SUBSCRIPTION_ID_RESPONSE_PATTERN);
    }
}
