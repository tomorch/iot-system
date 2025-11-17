package com.iot.simulator.generator;

import com.iot.model.SensorReading;
import com.iot.simulator.config.DeviceConfig;
import com.iot.simulator.config.SensorGroupConfig;
import com.iot.simulator.utils.DateFormat;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Component
public class ThermostatReadingGenerator implements ISensorReadingGenerator {
    private static final Random random = new Random();
    private static final String UNIT = "C";

    @Override
    public SensorReading generateSensorReading(SensorGroupConfig sensorGroupConfig, DeviceConfig deviceConfig) {
        double temperature = 15 + random.nextDouble() * 20; // 15-35°C

        return new SensorReading(
            UUID.randomUUID().toString(),
            deviceConfig.id(),
            sensorGroupConfig.type(),
            deviceConfig.groupId(),
            Math.round(temperature * 100.0) / 100.0,
            UNIT,
            DateFormat.format(LocalDateTime.now())
        );
    }
}
