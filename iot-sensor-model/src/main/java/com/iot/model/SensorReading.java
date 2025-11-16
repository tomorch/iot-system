package com.iot.model;

public record SensorReading(
        String id,
        String deviceId,
        String groupId,
        double value,
        String unit,
        String timestamp) {
}
