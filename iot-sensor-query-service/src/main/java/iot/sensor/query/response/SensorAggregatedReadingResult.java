package iot.sensor.query.response;

public record SensorAggregatedReadingResult(double mean, double median, double min, double max, int count) { }
