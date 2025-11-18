package com.iot.simulator.generator;

import com.iot.model.SensorReading;
import com.iot.simulator.config.SensorConfig;
import com.iot.simulator.config.SensorGroupConfig;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
public class FuelGaugeReadingGenerator implements ISensorReadingGenerator {
    private static final Random random = new Random();

    @Override
    public SensorReading generateSensorReading(SensorGroupConfig sensorGroupConfig, SensorConfig sensorConfig) {
        double fuelLevel = random.nextDouble() * 100; // 0-100%

        return new SensorReading(
            UUID.randomUUID().toString(),
            sensorConfig.id(),
            sensorGroupConfig.type(),
            sensorConfig.groupId(),
            Math.round(fuelLevel * 100.0) / 100.0,
            System.currentTimeMillis()
        );
    }
}
