package iot.sensor.query.response;

public record SensorQueryResult(SensorInfo sensorInfo, SensorAggregatedReadingResult aggregated) { }