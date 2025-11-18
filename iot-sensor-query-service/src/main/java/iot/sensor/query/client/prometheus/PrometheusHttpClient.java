package iot.sensor.query.client.prometheus;

import com.fasterxml.jackson.databind.ObjectMapper;
import iot.sensor.query.client.prometheus.response.PrometheusQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class PrometheusHttpClient {
    private static final Logger log = LoggerFactory.getLogger(PrometheusHttpClient.class);

    @Autowired
    private PrometheusHttpClientConfig config;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PrometheusQueryResponse sendQuery(String path, String query) {
        try {
            log.info("sending prometheus query to path: {}, query: {}", path, query);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(config.getBaseUrl() + path))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(query))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                log.debug("prometheus query response: {}", response.body());
                return objectMapper.readValue(response.body(), PrometheusQueryResponse.class);
            } else {
                log.error("prometheus query response code: {}, body: {}", response.statusCode(), response.body());
                return null;
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            log.error("prometheus query exception:", e);
            return null;
        }
    }
}
