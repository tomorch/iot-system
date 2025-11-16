package com.iot.simulator.model;

public record SensorReading(
        String id,
        String deviceId,
        String groupId,
        double value,
        String unit,
        String timestamp) {
}
