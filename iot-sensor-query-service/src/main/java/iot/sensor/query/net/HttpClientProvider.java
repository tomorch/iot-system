package iot.sensor.query.net;

import org.springframework.stereotype.Component;

import java.net.http.HttpClient;

@Component
public class HttpClientProvider implements IHttpClientProvider {
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public HttpClient getHttpClient() {
        return httpClient;
    }
}
