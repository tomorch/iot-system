package com.iot.simulator.config;

import java.util.List;

public record SensorGroupConfig(String type, String topic, List<SensorConfig> sensorConfigs) { }