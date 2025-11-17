package iot.sensor.query.model;

public record QueryResult(double mean, double median, double min, double max, long count) { }