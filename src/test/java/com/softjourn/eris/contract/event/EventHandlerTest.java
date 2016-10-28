package com.softjourn.eris.contract.event;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventHandlerTest  {

    private static final String SUBS1_ID = "0CFBA5B884D8D838A82288129E214F2CC3162C8965B53A046367C4458DCD77CE";
    private static final String SUBS2_ID = "DF98E65884D8D838A822881298914F2CC3162C8965B53A046367C4458DC45896";

    @Mock
    Consumer<String> consumer1;
    @Mock
    Consumer<String> consumer2;

    @Mock
    WebSocketFactory factory;

    @Mock
    WebSocket socket;

    WebSocketListener listener;

    AtomicBoolean isCalledgenId;

    EventHandler eventHandler;

    @Before
    public void setUp() throws Exception {

        isCalledgenId = new AtomicBoolean(false);

        when(factory.createSocket("http://real.url/socketrpc")).thenReturn(socket);

        when(socket.connect()).thenReturn(socket);

        when(socket.addListener(any()))
                .then(i -> {
                    listener = (WebSocketListener) i.getArguments()[0];
                    return socket;
                });

        when(socket.sendText(matches(".*erisdb.eventSubscribe.*")))
                .then(i -> {
                    String message = (String) i.getArguments()[0];
                    String id = getByPattern("^.*id\":\\s?\"([0-9a-fA-F]+)\".*$", message, 1);
                    listener.onTextMessage(socket, genResponse(id));
                    return socket;
                });

        eventHandler = Mockito.spy(new EventHandler("http://real.url", factory));

        when(eventHandler.genId()).then(i -> {
            if(isCalledgenId.get()) {
                return "2";
            } else {
                isCalledgenId.set(true);
                return "1";
            }
        });

        when(eventHandler.getId(anyString())).thenCallRealMethod();
        when(eventHandler.isThisSubscriptionId(anyString())).thenCallRealMethod();
        when(eventHandler.getSubscriptionId(anyString())).thenCallRealMethod();


    }

    private String genResponse(String id) {
        return "{" +
                "\"result\":" +
                "   {\"sub_id\":\"" + (id.equals("1") ? SUBS1_ID : SUBS2_ID) + "\"}," +
                "\"error\":null," +
                "\"id\":\"" + id + "\"," +
                "\"jsonrpc\":\"2.0\"}";
    }

    private String genUnsubscribeResponse(String id) {
        return "{" +
                "\"result\":" +
                "   {\"sub_id\":\"" + (id.equals("1") ? SUBS1_ID : SUBS2_ID) + "\"}," +
                "\"error\":null," +
                "\"id\":\"" + id + "\"," +
                "\"jsonrpc\":\"2.0\"}";
    }

    private String genEvent(String subsId) {
        return "{\"result\":" +
                "   [3," +
                "       {\"tx\":" +
                "       [2," +
                "           {\"input\":{\"address\":\"9B95D77DF5E60507BA1E42955BE02BC47C3A911C\"," +
                "                       \"amount\":1," +
                "                       \"sequence\":20," +
                "                       \"signature\":[1,\"5AF0739CD32E7A7AAA7CCD758CE3C74AE4BDC86EF033FF8C09BAE742C55257AC5EA25AA434B1501E066B09B678CC40ED8B0C4BEC46913B4DADC2D73F0DE8A708\"]," +
                "                       \"pub_key\":null" +
                "                       }," +
                "           \"address\":\"1F6749D464E5016B058A48F8A2AB8828F6FDE1D9\"," +
                "           \"gas_limit\":1000," +
                "           \"fee\":0," +
                "           \"data\":\"D0679D340000000000000000000000001F6749D464E5016B058A48F8A2AB8828F6FDE1D900000000000000000000000000000000000000000000000000000000000003E8\"" +
                "       }]," +
                "       \"return\":\"\"," +
                "       \"exception\":\"\"" +
                "   }]," +
                "   \"error\":null," +
                "   \"id\":\"" + subsId + "\"," +
                "   \"jsonrpc\":\"2.0\"}";
    }

    private String getByPattern(String pattern, String data, int group) {
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(data);
        if(matcher.find()) {
            return matcher.group(group);
        }
        throw new RuntimeException("Don't maches");
    }

    @Test(expected = EventSubscriptionException.class)
    public void testCreateCantEstablisheConnection() throws WebSocketException {
        when(socket.connect()).thenThrow(new WebSocketException(null, ""));

        EventHandler eventHandler = new EventHandler("http://real.url", factory);
        eventHandler.subscribe("id", (s -> System.out.println(s)));
    }

    @Test(expected = EventSubscriptionException.class)
    public void testSubscribeTimeOut() throws WebSocketException {
        when(socket.sendText(matches(".*erisdb.eventSubscribe.*"))).then(i -> socket);

        String res = eventHandler.subscribe("someEvent1", d -> {});
    }

    @Test
    public void testSubscribe() throws Exception {

        String sid1 = eventHandler.subscribe("someEvent1", consumer1);
        String sid2 = eventHandler.subscribe("someEvent2", consumer2);

        assertEquals(SUBS1_ID, sid1);
        assertEquals(SUBS2_ID, sid2);

        //Imitate event for subscription id 1
        //consumer1 should be called consumer2 not
        listener.onTextMessage(socket, genEvent(SUBS1_ID));
        verify(consumer1, times(1)).accept(eq(genEvent(SUBS1_ID)));
        verify(consumer2, times(0)).accept(anyString());

        //Imitate event for subscription id 2
        //consumer1 and consumer2 should be called each one time(including previous)
        listener.onTextMessage(socket, genEvent(SUBS2_ID));
        verify(consumer1, times(1)).accept(eq(genEvent(SUBS1_ID)));
        verify(consumer2, times(1)).accept(eq(genEvent(SUBS2_ID)));

        //unsubscribe consumer1
        eventHandler.unsubscribe(SUBS1_ID);

        //Imitate event for subscription id 1
        //none consumer is called
        listener.onTextMessage(socket, genEvent(SUBS1_ID));
        verify(consumer1, times(1)).accept(eq(genEvent(SUBS1_ID)));
        verify(consumer2, times(1)).accept(anyString());

        //Imitate event for subscription id 2
        //consumer2 should be called one mor time
        listener.onTextMessage(socket, genEvent(SUBS2_ID));
        verify(consumer1, times(1)).accept(eq(genEvent(SUBS1_ID)));
        verify(consumer2, times(2)).accept(eq(genEvent(SUBS2_ID)));
    }


}