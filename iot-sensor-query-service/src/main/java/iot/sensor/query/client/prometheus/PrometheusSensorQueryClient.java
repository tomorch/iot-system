package iot.sensor.query.client.prometheus;

import iot.sensor.query.client.ISensorQueryClient;
import iot.sensor.query.client.prometheus.response.PrometheusQueryResponse;
import iot.sensor.query.model.SensorGroupQueryResult;
import iot.sensor.query.model.SensorQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrometheusSensorQueryClient implements ISensorQueryClient {
    private static final int DEFAULT_STEP_SECONDS = 15;
    private static final String QUERY_PATH = "/query_range";

    @Autowired
    private PrometheusHttpClient httpClient;

    @Autowired
    private PrometheusResponseTransformer responseTransformer;

    @Override
    public SensorQueryResult executeSensorQuery(String sensorId, String start, String end) {
        String query = String.format("query=sensor_value{sensor_id=\"%s\"}", sensorId)
            + String.format("&step=%ds", DEFAULT_STEP_SECONDS)
            + String.format("&start=%s", start)
            + String.format("&end=%s", end);

        PrometheusQueryResponse response = httpClient.sendQuery(QUERY_PATH, query);

        if(response == null || !response.status().equals("success") ) {
            throw new RuntimeException("failed to query prometheus");
        } else {
            return responseTransformer.transformToSensorQueryResult(sensorId, response);
        }
    }

    @Override
    public SensorGroupQueryResult executeSensorGroupQuery(String groupId, String start, String end) {
        String query = String.format("query=sensor_value{group_id=\"%s\"}", groupId)
            + String.format("&step=%ds", DEFAULT_STEP_SECONDS)
            + String.format("&start=%s", start)
            + String.format("&end=%s", end);

        PrometheusQueryResponse response = httpClient.sendQuery(QUERY_PATH, query);

        if(response == null || !response.status().equals("success") ) {
            throw new RuntimeException("failed to query prometheus");
        } else {
            return responseTransformer.transformToSensorGroupQueryResult(groupId, response);
        }
    }
}
