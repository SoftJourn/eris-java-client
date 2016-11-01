package com.softjourn.eris.rpc;


import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

/**
 * Rpc client that make calls via Http
 */
public class HTTPRPCClient implements RPCClient {

    private final String URL;

    public HTTPRPCClient(String host) {
        this.URL = host;
    }

    @Override
    public String call(RPCRequestEntity entity) throws IOException {
        HttpResponse response =  makeRequest(URL + "/rpc", entity.toString());
        try {
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() >= 400) throw new IOException(statusLine.getReasonPhrase());
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new IOException("Can't read response from server due to exception.", e);
        }
    }

    private HttpResponse makeRequest(String URL, String body) throws IOException {
        try {
            HttpClient client = HttpClientBuilder
                    .create()
                    .disableAutomaticRetries()
                    .build();

            HttpPost request = new HttpPost();
            request.setEntity(new StringEntity(body));
            request.setURI(new URI(URL));
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            return client.execute(request);

        } catch (IOException | URISyntaxException e) {
            throw new IOException("Can't make RPC request due to exception.", e);
        }
    }

}
