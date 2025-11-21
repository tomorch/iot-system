package iot.sensor.query.net;

import java.net.http.HttpClient;

public interface IHttpClientProvider {
    public HttpClient getHttpClient();
}
