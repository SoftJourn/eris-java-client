package com.softjourn.eris.contract;

import com.softjourn.eris.contract.response.Error;
import com.softjourn.eris.contract.response.Response;
import com.softjourn.eris.contract.response.ReturnValue;
import com.softjourn.eris.contract.response.TxParams;
import com.softjourn.eris.contract.types.Uint;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class ResponseParserTest {

    private Response<BigInteger> txResponse;

    private Response txResponse_V_12;

    private Response<BigInteger> txResponseWithRetValue;

    private Response<BigInteger> valResponse;

    private Response errorResponse;

    private ResponseParser parser;

    private final String valResponseString = "{\n" +
            "    \"result\": {\n" +
            "      \"return\": \"0000000000000000000000000000000000000000000000000000000000000064\",\n" +
            "      \"gas_used\": 0\n" +
            "    },\n" +
            "    \"error\": null,\n" +
            "    \"id\": \"\",\n" +
            "    \"jsonrpc\": \"2.0\"\n" +
            "  }";

    private final String txResponseSring = "{\n" +
            "    \"result\": [\n" +
            "      4,\n" +
            "      {\n" +
            "        \"call_data\": {\n" +
            "          \"caller\": \"1ADA404B3EEDD5CC971475489A17BAACB9BA5D68\",\n" +
            "          \"callee\": \"628FF6F4BB9C68A8BF9C2BBE04242C4B7FAE6A19\",\n" +
            "          \"data\": \"D0679D340000000000000000000000007EA0B39261842504023A40858099700E061563CE0000000000000000000000000000000000000000000000000000000000000001\",\n" +
            "          \"value\": 1,\n" +
            "          \"gas\": 9701\n" +
            "        },\n" +
            "        \"origin\": \"1ADA404B3EEDD5CC971475489A17BAACB9BA5D68\",\n" +
            "        \"tx_id\": \"8993486AB880DB2144A58989B4E3D72F9656246D\",\n" +
            "        \"return\": \"\",\n" +
            "        \"exception\": \"\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"error\": null,\n" +
            "    \"id\": \"\",\n" +
            "    \"jsonrpc\": \"2.0\"\n" +
            "  }";

    private final String txResponseWithRetValueSring = "{\n" +
            "    \"result\": [\n" +
            "      4,\n" +
            "      {\n" +
            "        \"call_data\": {\n" +
            "          \"caller\": \"1ADA404B3EEDD5CC971475489A17BAACB9BA5D68\",\n" +
            "          \"callee\": \"628FF6F4BB9C68A8BF9C2BBE04242C4B7FAE6A19\",\n" +
            "          \"data\": \"D0679D340000000000000000000000007EA0B39261842504023A40858099700E061563CE0000000000000000000000000000000000000000000000000000000000000001\",\n" +
            "          \"value\": 1,\n" +
            "          \"gas\": 9701\n" +
            "        },\n" +
            "        \"origin\": \"1ADA404B3EEDD5CC971475489A17BAACB9BA5D68\",\n" +
            "        \"tx_id\": \"8993486AB880DB2144A58989B4E3D72F9656246D\",\n" +
            "        \"return\": \"0000000000000000000000000000000000000000000000000000000000000064\",\n" +
            "        \"exception\": \"\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"error\": null,\n" +
            "    \"id\": \"\",\n" +
            "    \"jsonrpc\": \"2.0\"\n" +
            "  }";

    private final String txResponseEris_V_12 = "{\n" +
            "  \"result\": {\n" +
            "    \"call_data\": {\n" +
            "      \"caller\": \"6F4090E6FBB579CFFCCDF56CC0F74EFA2A3ADD5E\",\n" +
            "      \"callee\": \"92639EEA8028DD04DBE16F186962400C5CF64DD9\",\n" +
            "      \"data\": \"40C10F190000000000000000000000009D370D7A53B844F59CE127E30A86982707BAC89E0000000000000000000000000000000000000000000000000000000000000064\",\n" +
            "      \"value\": 1,\n" +
            "      \"gas\": 832\n" +
            "    },\n" +
            "    \"origin\": \"6F4090E6FBB579CFFCCDF56CC0F74EFA2A3ADD5E\",\n" +
            "    \"tx_id\": \"E0B8E0A8005FDC681D8836F9FE1BAA3CC4B82AE7\",\n" +
            "    \"return\": \"\",\n" +
            "    \"exception\": \"\"\n" +
            "  },\n" +
            "  \"error\": null,\n" +
            "  \"id\": \"\",\n" +
            "  \"jsonrpc\": \"2.0\"\n" +
            "}";

    private final String errorResponseString = "{\n" +
            "    \"result\": null,\n" +
            "    \"error\": {\n" +
            "      \"code\": -32603,\n" +
            "      \"message\": \"Error when transacting: Insuffient gas\"\n" +
            "    },\n" +
            "    \"id\": \"\",\n" +
            "    \"jsonrpc\": \"2.0\"\n" +
            "  }";

    @Before
    public void setUp() throws Exception {
        txResponse = new Response<>("", null, null, new TxParams("1ADA404B3EEDD5CC971475489A17BAACB9BA5D68", "8993486AB880DB2144A58989B4E3D72F9656246D"));
        txResponse_V_12 = new Response<>("", null, null, new TxParams("6F4090E6FBB579CFFCCDF56CC0F74EFA2A3ADD5E", "E0B8E0A8005FDC681D8836F9FE1BAA3CC4B82AE7"));
        txResponseWithRetValue = new Response<>("", new ReturnValue<>(BigInteger.class, BigInteger.valueOf(100L)), null, new TxParams("1ADA404B3EEDD5CC971475489A17BAACB9BA5D68", "8993486AB880DB2144A58989B4E3D72F9656246D"));
        valResponse = new Response<>("", new ReturnValue<>(BigInteger.class, BigInteger.valueOf(100L)), null, null);
        errorResponse = new Response<>("", null, new Error(-32603, "Error when transacting: Insuffient gas"), null);
    }

    @Test
    public void testParseVal() throws Exception {
        ResponseParser<BigInteger> parser = new ResponseParser<>(new Variable<>("x", new Uint()));
        assertEquals(valResponse, parser.parse(valResponseString));
    }

    @Test
    public void testParseError() throws IOException {
        ResponseParser parser = new ResponseParser<>(null);
        assertEquals(errorResponse, parser.parse(errorResponseString));
    }

    @Test
    public void testParseTx() throws IOException {
        ResponseParser parser = new ResponseParser<>(null);
        assertEquals(txResponse, parser.parse(txResponseSring));
    }

    @Test
    public void testParseTx_Eris_V_12() throws IOException {
        ResponseParser parser = new ResponseParser<>(null);
        assertEquals(txResponse_V_12, parser.parse(txResponseEris_V_12));
    }

    @Test
    public void testParseTxWithRetVal() throws IOException {
        ResponseParser parser = new ResponseParser<>(new Variable<>("x", new Uint()));
        assertEquals(txResponseWithRetValue, parser.parse(txResponseWithRetValueSring));
    }

}