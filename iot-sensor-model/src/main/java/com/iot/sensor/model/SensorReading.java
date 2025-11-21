package com.iot.sensor.model;

public record SensorReading(
        String id,
        String sensorId,
        String sensorType,
        String groupId,
        double value,
        long timestamp) {
}
