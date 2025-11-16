package com.iot.simulator.generator;

import com.iot.model.SensorReading;
import com.iot.simulator.config.DeviceConfig;
import com.iot.simulator.config.SensorGroupConfig;

public interface ISensorReadingGenerator {
    SensorReading generateSensorReading(SensorGroupConfig sensorGroupConfig, DeviceConfig deviceConfig);
}
