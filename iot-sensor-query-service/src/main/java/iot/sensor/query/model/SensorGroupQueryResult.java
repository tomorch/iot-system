package iot.sensor.query.model;

public record SensorGroupQueryResult(String groupId, SensorAggregatedReadingResult aggregated) { }