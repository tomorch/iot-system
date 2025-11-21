package com.iot.sensor.collector.topology;

import com.iot.sensor.model.SensorReading;
import org.apache.flink.connector.prometheus.sink.PrometheusTimeSeries;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SensorReadingToPrometheusTimeSeriesMapperTest {
    @Test
    public void testMappingFunction() {
        String readingId = "somereadingid";
        String sensorId = "somesensorid";
        String sensorType = "somesensortype";
        String groupId = "somegroupid";
        double value = 10.5;
        long timestamp = System.currentTimeMillis();

        // given a sensor reading
        SensorReading sensorReading = new SensorReading(readingId, sensorId, sensorType, groupId, value, timestamp);

        // when map function is executed
        SensorReadingToPrometheusTimeSeriesMapper mapper = new SensorReadingToPrometheusTimeSeriesMapper();

        // then time series should be returned
        PrometheusTimeSeries prometheusTimeSeries = mapper.map(sensorReading);

        // and value and timestamps should match
        assertEquals(value, prometheusTimeSeries.getSamples()[0].getValue());
        assertEquals(timestamp, prometheusTimeSeries.getSamples()[0].getTimestamp());

        // and all labels should match
        assertLabel(prometheusTimeSeries, "reading_id", readingId);
        assertLabel(prometheusTimeSeries, "sensor_type", sensorType);
        assertLabel(prometheusTimeSeries, "group_id", groupId);
        assertLabel(prometheusTimeSeries, "sensor_id", sensorId);
    }

    private void assertLabel(PrometheusTimeSeries prometheusTimeSeries, String labelName, String expectedValue) {
        assertEquals(expectedValue, Arrays.stream(prometheusTimeSeries.getLabels())
            .filter(filter -> filter.getName().equals(labelName))
            .findFirst().map(PrometheusTimeSeries.Label::getValue).orElse(null)
        );
    }
}
