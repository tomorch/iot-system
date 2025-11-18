package iot.sensor.query.model;

public record SensorQueryResult(SensorInfo sensorInfo, double mean, double median, double min, double max, long count) { }