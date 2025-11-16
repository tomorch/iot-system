package com.iot.simulator.generator;

import com.iot.simulator.config.DeviceConfig;
import com.iot.simulator.config.SensorGroupConfig;
import com.iot.simulator.model.SensorReading;

public interface ISensorReadingGenerator {
    SensorReading generateSensorReading(SensorGroupConfig sensorGroupConfig, DeviceConfig deviceConfig);
}
