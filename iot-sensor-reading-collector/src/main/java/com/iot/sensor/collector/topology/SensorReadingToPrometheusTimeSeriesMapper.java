package com.iot.sensor.collector.topology;

import com.iot.sensor.model.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.connector.prometheus.sink.PrometheusTimeSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorReadingToPrometheusTimeSeriesMapper implements MapFunction<SensorReading, PrometheusTimeSeries> {
    private static final Logger log = LoggerFactory.getLogger(SensorReadingToPrometheusTimeSeriesMapper.class);

    @Override
    public PrometheusTimeSeries map(SensorReading sensorReading) {
        log.info("mapping reading to prometheus ts: {}", sensorReading);

        return PrometheusTimeSeries.builder()
            .withMetricName("sensor_value")
            .addLabel("sensor_id", sensorReading.sensorId())
            .addLabel("sensor_type", sensorReading.sensorType())
            .addLabel("group_id", sensorReading.groupId())
            .addSample(sensorReading.value(), sensorReading.timestamp())
            .build();
    }
}
