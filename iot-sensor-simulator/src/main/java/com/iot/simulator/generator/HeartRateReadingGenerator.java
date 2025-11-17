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
public class HeartRateReadingGenerator implements ISensorReadingGenerator {
    private static final Random random = new Random();
    private static final String UNIT = "bpm";

    @Override
    public SensorReading generateSensorReading(SensorGroupConfig sensorGroupConfig, DeviceConfig deviceConfig) {
        double heartRate = 50 + random.nextDouble() * 100; // 50-150 bpm

        return new SensorReading(
            UUID.randomUUID().toString(),
            deviceConfig.id(),
            sensorGroupConfig.type(),
            deviceConfig.groupId(),
            Math.round(heartRate),
            UNIT,
            DateFormat.format(LocalDateTime.now())
        );
    }
}
