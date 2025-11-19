package iot.sensor.query.response;

public record SensorGroupQueryResult(String groupId, SensorAggregatedReadingResult aggregated) { }