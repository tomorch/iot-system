package com.iot.sensor.simulator.scheduler;

import com.iot.sensor.simulator.generator.SensorReadingGenerator;
import com.iot.sensor.simulator.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.jmx.export.annotation.ManagedResource;

@Component
@RequiredArgsConstructor
@ManagedResource
public class SensorSimulationScheduler {
    private static final Logger log = LoggerFactory.getLogger(SensorSimulationScheduler.class);

    private final KafkaProducerService kafkaProducerService;

    private final SensorReadingGenerator sensorReadingGenerator;

    @Scheduled(fixedRate = 1000)
    public void publishSensorReadings() {
        sensorReadingGenerator.generateReadings().forEach(generatedReading -> {
            log.info("Publishing sensor reading {}", generatedReading);

            kafkaProducerService.publishReading(generatedReading.topic(), generatedReading.sensorReading());
        });
    }
}
