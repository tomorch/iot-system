package iot.sensor.query.client.prometheus;

import iot.sensor.query.client.prometheus.response.*;
import iot.sensor.query.response.SensorGroupQueryResult;
import iot.sensor.query.response.SensorQueryResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrometheusResponseTransformerTest {
    @Test
    public void testTransform3ResultPrometheusQueryResponse() {
        String sensorId = "somesensorid";
        String sensorType = "somesensortype";
        String groupId = "somegroup";
        String readingId = "somereadingid";

        PrometheusResponseTransformer responseTransformer = new PrometheusResponseTransformer();

        List<PrometheusResult> result = new ArrayList<>();
        PrometheusMetric metric = new PrometheusMetric("sensor_value", sensorId, sensorType, groupId, readingId);

        result.add(createResult(metric,"13.50"));
        result.add(createResult(metric,"25.25"));
        result.add(createResult(metric,"17"));

        PrometheusData data = new PrometheusData("matrix", result);
        PrometheusQueryResponse queryResponse = new PrometheusQueryResponse("success", data);

        SensorQueryResult sensorQueryResult =  responseTransformer.transformToSensorQueryResult(sensorId, queryResponse);

        assertEquals(sensorId, sensorQueryResult.sensorInfo().id());
        assertEquals(sensorType, sensorQueryResult.sensorInfo().sensorType());
        assertEquals(groupId, sensorQueryResult.sensorInfo().groupId());
        assertEquals(13.50, sensorQueryResult.aggregated().min());
        assertEquals(25.25, sensorQueryResult.aggregated().max());
        assertEquals(3, sensorQueryResult.aggregated().count());
        assertEquals(18.58, sensorQueryResult.aggregated().mean());
        assertEquals(17, sensorQueryResult.aggregated().median());
    }

    @Test
    public void testTransform4ResultPrometheusQueryResponse() {
        String sensorId = "somesensorid";
        String sensorType = "somesensortype";
        String groupId = "somegroup";
        String readingId = "somereadingid";

        PrometheusResponseTransformer responseTransformer = new PrometheusResponseTransformer();

        List<PrometheusResult> result = new ArrayList<>();
        PrometheusMetric metric = new PrometheusMetric("sensor_value", sensorId, sensorType, groupId, readingId);

        result.add(createResult(metric,"28.8"));
        result.add(createResult(metric,"22.25"));
        result.add(createResult(metric,"19"));
        result.add(createResult(metric,"33.333"));

        PrometheusData data = new PrometheusData("matrix", result);
        PrometheusQueryResponse queryResponse = new PrometheusQueryResponse("success", data);

        SensorQueryResult sensorQueryResult =  responseTransformer.transformToSensorQueryResult(sensorId, queryResponse);

        assertEquals(sensorId, sensorQueryResult.sensorInfo().id());
        assertEquals(sensorType, sensorQueryResult.sensorInfo().sensorType());
        assertEquals(groupId, sensorQueryResult.sensorInfo().groupId());
        assertEquals(19, sensorQueryResult.aggregated().min());
        assertEquals(33.333, sensorQueryResult.aggregated().max());
        assertEquals(4, sensorQueryResult.aggregated().count());
        assertEquals(25.85, sensorQueryResult.aggregated().mean());
        assertEquals(25.53, sensorQueryResult.aggregated().median());
    }

    @Test
    public void testTransform3ResultPrometheusGroupQueryResponse() {
        String sensorId = "somesensorid";
        String sensorType = "somesensortype";
        String groupId = "somegroup";
        String readingId = "somereadingid";

        PrometheusResponseTransformer responseTransformer = new PrometheusResponseTransformer();

        List<PrometheusResult> result = new ArrayList<>();
        PrometheusMetric metric = new PrometheusMetric("sensor_value", sensorId, sensorType, groupId, readingId);

        result.add(createResult(metric,"13.50"));
        result.add(createResult(metric,"25.25"));
        result.add(createResult(metric,"17"));

        PrometheusData data = new PrometheusData("matrix", result);
        PrometheusQueryResponse queryResponse = new PrometheusQueryResponse("success", data);

        SensorGroupQueryResult sensorGroupQueryResult =  responseTransformer.transformToSensorGroupQueryResult(groupId, queryResponse);

        assertEquals(groupId, sensorGroupQueryResult.groupId());
        assertEquals(13.50, sensorGroupQueryResult.aggregated().min());
        assertEquals(25.25, sensorGroupQueryResult.aggregated().max());
        assertEquals(3, sensorGroupQueryResult.aggregated().count());
        assertEquals(18.58, sensorGroupQueryResult.aggregated().mean());
        assertEquals(17, sensorGroupQueryResult.aggregated().median());
    }

    @Test
    public void testTransform4ResultPrometheusGroupQueryResponse() {
        String sensorId = "somesensorid";
        String sensorType = "somesensortype";
        String groupId = "somegroup";
        String readingId = "somereadingid";

        PrometheusResponseTransformer responseTransformer = new PrometheusResponseTransformer();

        List<PrometheusResult> result = new ArrayList<>();
        PrometheusMetric metric = new PrometheusMetric("sensor_value", sensorId, sensorType, groupId, readingId);

        result.add(createResult(metric,"28.8"));
        result.add(createResult(metric,"22.25"));
        result.add(createResult(metric,"19"));
        result.add(createResult(metric,"33.333"));

        PrometheusData data = new PrometheusData("matrix", result);
        PrometheusQueryResponse queryResponse = new PrometheusQueryResponse("success", data);

        SensorGroupQueryResult sensorGroupQueryResult =  responseTransformer.transformToSensorGroupQueryResult(groupId, queryResponse);

        assertEquals(groupId, sensorGroupQueryResult.groupId());
        assertEquals(19, sensorGroupQueryResult.aggregated().min());
        assertEquals(33.333, sensorGroupQueryResult.aggregated().max());
        assertEquals(4, sensorGroupQueryResult.aggregated().count());
        assertEquals(25.85, sensorGroupQueryResult.aggregated().mean());
        assertEquals(25.53, sensorGroupQueryResult.aggregated().median());
    }

    private PrometheusResult createResult(PrometheusMetric metric, String value) {
        PrometheusValue[] values = new PrometheusValue[1];
        values[0] = new PrometheusValue(System.currentTimeMillis(), value);
        return new PrometheusResult(metric, values);
    }
}
