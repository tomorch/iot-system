package iot.sensor.query.client.prometheus;

import iot.sensor.query.client.ISensorQueryClient;
import iot.sensor.query.client.prometheus.response.PrometheusQueryResponse;
import iot.sensor.query.model.SensorQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
public class PrometheusSensorQueryClient implements ISensorQueryClient {
    private static final int DEFAULT_STEP_SECONDS = 15;
    private static final String QUERY_PATH = "/query_range";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Autowired
    private PrometheusHttpClient httpClient;

    @Autowired
    private PrometheusResponseTransformer responseTransformer;

    @Override
    public SensorQueryResult executeSensorQuery(String sensorId, String start, String end) {
        try {
            dateFormat.parse(start);
            dateFormat.parse(end);
        } catch (Exception e) {
            throw new RuntimeException("failed to query prometheus - invalid date format for either start or end date");
        }

        String query = String.format("query=sensor_value{sensor_id=\"%s\"}", sensorId)
            + String.format("&step=%ds", DEFAULT_STEP_SECONDS)
            + String.format("&start=%s", start)
            + String.format("&end=%s", end);

        PrometheusQueryResponse response = httpClient.sendQuery(QUERY_PATH, query);

        if(response == null || !response.status().equals("success") ) {
            throw new RuntimeException("failed to query prometheus");
        } else {
            return responseTransformer.transformToSensorQueryResult(response);
        }
    }
}
