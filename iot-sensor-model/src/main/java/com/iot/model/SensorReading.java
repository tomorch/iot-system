package com.iot.model;

public record SensorReading(
        String id,
        String deviceId,
        String sensorType,
        String groupId,
        double value,
        String unit,
        long timestamp) {
}
