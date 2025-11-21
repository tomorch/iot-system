package com.iot.simulator.generator;

import com.iot.model.SensorReading;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class SensorReadingGenerator {
    @Autowired
    private SensorReadingGeneratorConfig sensorReadingGeneratorConfig;

    public List<SensorGeneratedReading> generateReadings() {
        final Random rand = new Random();

        return sensorReadingGeneratorConfig.getSensorTypeConfigs().stream()
            .flatMap(sensorConfig -> {
                List<SensorGeneratedReading> generatedReadings = new ArrayList<>();
                for(int i = 0; i < sensorConfig.getNumSensors(); i++) {
                    double value = sensorConfig.getMinValue() + rand.nextDouble(sensorConfig.getMaxValue() - sensorConfig.getMinValue());
                    generatedReadings.add(
                        new SensorGeneratedReading(
                            new SensorReading(
                                UUID.randomUUID().toString(),
                                sensorConfig.getSensorIdPrefix() + i,
                                sensorConfig.getSensorType(),
                                sensorConfig.getGroupIds().get(rand.nextInt(sensorConfig.getGroupIds().size())),
                                Math.round(value * 100.0) / 100.0,
                                System.currentTimeMillis()
                            ),
                            sensorConfig.getTopic()
                        )
                    );
                }
                return generatedReadings.stream();
            }).toList();
    }
}
