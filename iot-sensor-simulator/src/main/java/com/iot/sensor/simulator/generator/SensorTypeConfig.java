package com.iot.sensor.simulator.generator;

import java.util.List;

public record SensorTypeConfig(
    String sensorType,
    String sensorIdPrefix,
    int numSensors,
    double minValue,
    double maxValue,
    List<String> groupIds,
    String topic) {
}