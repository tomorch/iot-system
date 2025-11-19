package iot.sensor.query.client.prometheus;

import iot.sensor.query.client.prometheus.response.PrometheusQueryResponse;
import iot.sensor.query.client.prometheus.response.PrometheusResult;
import iot.sensor.query.net.IHttpClientProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@SpringBootTest
public class PrometheusHttpClientTest {
    @Mock
    private PrometheusHttpClientConfig httpClientConfig;

    @MockitoBean
    private IHttpClientProvider httpClientProvider;

    @Autowired
    private PrometheusHttpClient prometheusHttpClient;

    @Test
    public void testValidRequest() throws IOException, InterruptedException {
        String baseUrl = "http://localhost:8080";

        Mockito.when(httpClientConfig.getBaseUrl()).thenReturn(baseUrl);

        HttpResponse<String> response = Mockito.mock(HttpResponse.class);
        Mockito.when(response.statusCode()).thenReturn(200);
        Mockito.when(response.body()).thenReturn("{\"status\":\"success\",\"data\":{\"resultType\":\"matrix\",\"result\":[{\"metric\":{\"__name__\":\"sensor_value\",\"group_id\":\"fitness\",\"reading_id\":\"0628a2c9-b9cd-4ca5-a6fe-61814a03b048\",\"sensor_id\":\"hm1\",\"sensor_type\":\"HEART_RATE_MONITOR\"},\"values\":[[1763545665,\"140.69\"],[1763545680,\"135.09\"],[1763545695,\"145.70\"]]}]}}");

        HttpClient httpClient = Mockito.mock(HttpClient.class);
        Mockito.when(httpClient.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class))).thenReturn(response);

        Mockito.when(httpClientProvider.getHttpClient()).thenReturn(httpClient);

        PrometheusQueryResponse queryResponse = prometheusHttpClient.sendQuery("somepath", "somequery");

        assertEquals("success", queryResponse.status());
        assertNotNull(queryResponse.data());
        assertNotNull(queryResponse.data().result());
        assertEquals("matrix", queryResponse.data().resultType());
        assertEquals(1, queryResponse.data().result().size());

        PrometheusResult first = queryResponse.data().result().get(0);

        assertEquals("hm1", first.metric().sensor_id());
        assertEquals("fitness", first.metric().group_id());
        assertEquals("0628a2c9-b9cd-4ca5-a6fe-61814a03b048", first.metric().reading_id());
        assertEquals("HEART_RATE_MONITOR", first.metric().sensor_type());

        assertEquals(1763545665, first.values()[0].timestamp());
        assertEquals("140.69", first.values()[0].value());

        assertEquals(1763545680, first.values()[1].timestamp());
        assertEquals("135.09", first.values()[1].value());

        assertEquals(1763545695, first.values()[2].timestamp());
        assertEquals("145.70", first.values()[2].value());

        Mockito.verify(httpClient, times(1)).send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class));
    }

    @Test
    public void testNon200ResponseCode() throws IOException, InterruptedException {
        String baseUrl = "http://localhost:8080";

        Mockito.when(httpClientConfig.getBaseUrl()).thenReturn(baseUrl);

        HttpResponse<String> response = Mockito.mock(HttpResponse.class);
        Mockito.when(response.statusCode()).thenReturn(500);

        HttpClient httpClient = Mockito.mock(HttpClient.class);
        Mockito.when(httpClient.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class))).thenReturn(response);

        Mockito.when(httpClientProvider.getHttpClient()).thenReturn(httpClient);

        PrometheusQueryResponse queryResponse = prometheusHttpClient.sendQuery("somepath", "somequery");

        assertNull(queryResponse);
    }

    @Test
    public void testIOExceptionThrown() throws IOException, InterruptedException {
        String baseUrl = "http://localhost:8080";

        Mockito.when(httpClientConfig.getBaseUrl()).thenReturn(baseUrl);

        HttpClient httpClient = Mockito.mock(HttpClient.class);
        Mockito.when(httpClient.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class))).thenThrow(new IOException());

        Mockito.when(httpClientProvider.getHttpClient()).thenReturn(httpClient);

        PrometheusQueryResponse queryResponse = prometheusHttpClient.sendQuery("somepath", "somequery");

        assertNull(queryResponse);
    }

    @Test
    public void testInterruptedExceptionThrown() throws IOException, InterruptedException {
        String baseUrl = "http://localhost:8080";

        Mockito.when(httpClientConfig.getBaseUrl()).thenReturn(baseUrl);

        HttpClient httpClient = Mockito.mock(HttpClient.class);
        Mockito.when(httpClient.send(Mockito.any(HttpRequest.class), Mockito.any(HttpResponse.BodyHandler.class))).thenThrow(new InterruptedException());

        Mockito.when(httpClientProvider.getHttpClient()).thenReturn(httpClient);

        PrometheusQueryResponse queryResponse = prometheusHttpClient.sendQuery("somepath", "somequery");

        assertNull(queryResponse);
    }
}
