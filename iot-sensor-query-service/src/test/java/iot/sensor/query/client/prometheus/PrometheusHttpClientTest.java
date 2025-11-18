package iot.sensor.query.client.prometheus;

import iot.sensor.query.client.prometheus.response.PrometheusQueryResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.net.http.HttpClient;

@SpringBootTest
public class PrometheusHttpClientTest {
    @Mock
    private PrometheusHttpClientConfig httpClientConfig;

    @MockitoSpyBean
    private PrometheusHttpClient prometheusHttpClient;

    @Test
    public void testPrometheusHttpClient() {
        Mockito.when(httpClientConfig.getBaseUrl()).thenReturn("http://localhost:8080");

        HttpClient httpClient = Mockito.mock(HttpClient.class);
        Mockito.when(prometheusHttpClient.getHttpClient()).thenReturn(httpClient);

        PrometheusQueryResponse queryResponse = prometheusHttpClient.sendQuery("somepath", "somequery");
    }
}
