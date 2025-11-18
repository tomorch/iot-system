package com.iot.simulator.generator;

import com.iot.model.SensorReading;
import com.iot.simulator.config.SensorConfig;
import com.iot.simulator.config.SensorGroupConfig;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
public class HeartRateReadingGenerator implements ISensorReadingGenerator {
    private static final Random random = new Random();
    private static final String UNIT = "bpm";

    @Override
    public SensorReading generateSensorReading(SensorGroupConfig sensorGroupConfig, SensorConfig sensorConfig) {
        double heartRate = 50 + random.nextDouble() * 100; // 50-150 bpm

        return new SensorReading(
            UUID.randomUUID().toString(),
            sensorConfig.id(),
            sensorGroupConfig.type(),
            sensorConfig.groupId(),
            Math.round(heartRate),
            System.currentTimeMillis()
        );
    }
}
