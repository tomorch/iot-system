package com.iot.simulator.config;

import java.util.List;

public record SimulatorConfig(long publishRateMs, List<SensorGroupConfig> sensorGroupConfigs) { }
