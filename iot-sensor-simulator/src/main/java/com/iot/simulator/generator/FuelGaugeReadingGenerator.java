package com.iot.simulator.generator;

import com.iot.model.SensorReading;
import com.iot.simulator.config.DeviceConfig;
import com.iot.simulator.config.SensorGroupConfig;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
public class FuelGaugeReadingGenerator implements ISensorReadingGenerator {
    private static final Random random = new Random();
    private static final String UNIT = "%";


    @Override
    public SensorReading generateSensorReading(SensorGroupConfig sensorGroupConfig, DeviceConfig deviceConfig) {
        double fuelLevel = random.nextDouble() * 100; // 0-100%

        return new SensorReading(
            UUID.randomUUID().toString(),
            deviceConfig.id(),
            sensorGroupConfig.type(),
            deviceConfig.groupId(),
            Math.round(fuelLevel * 100.0) / 100.0,
            UNIT,
            System.currentTimeMillis()
        );
    }
}
