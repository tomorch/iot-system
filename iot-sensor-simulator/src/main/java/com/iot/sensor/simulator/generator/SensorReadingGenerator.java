package com.iot.sensor.simulator.generator;

import com.iot.sensor.model.SensorReading;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class SensorReadingGenerator {
    private final SensorReadingGeneratorConfig sensorReadingGeneratorConfig;

    public List<SensorGeneratedReading> generateReadings() {
        final Random rand = new Random();

        return sensorReadingGeneratorConfig.getSensorTypeConfigs().stream()
            .flatMap(sensorConfig -> {
                List<SensorGeneratedReading> generatedReadings = new ArrayList<>();
                for(int i = 0; i < sensorConfig.numSensors(); i++) {
                    double value = sensorConfig.minValue() + rand.nextDouble(sensorConfig.maxValue() - sensorConfig.minValue());
                    generatedReadings.add(
                        new SensorGeneratedReading(
                            new SensorReading(
                                UUID.randomUUID().toString(),
                                sensorConfig.sensorIdPrefix() + i,
                                sensorConfig.sensorType(),
                                sensorConfig.groupIds().get(rand.nextInt(sensorConfig.groupIds().size())),
                                Math.round(value * 100.0) / 100.0,
                                System.currentTimeMillis()
                            ),
                            sensorConfig.topic()
                        )
                    );
                }
                return generatedReadings.stream();
            }).toList();
    }
}
