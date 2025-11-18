package iot.sensor.query.model;

public record SensorAggregatedReadingResult(double mean, double median, double min, double max, int count) { }
