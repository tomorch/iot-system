package com.iot.simulator.scheduler;

import com.iot.simulator.generator.SensorReadingGenerator;
import com.iot.simulator.service.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.jmx.export.annotation.ManagedResource;

@Component
@ManagedResource
public class SensorSimulationScheduler {
    private static final Logger log = LoggerFactory.getLogger(SensorSimulationScheduler.class);

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private SensorReadingGenerator sensorReadingGenerator;

    @Scheduled(fixedRate = 1000)
    public void publishSensorReadings() {
        sensorReadingGenerator.generateReadings().forEach(sensorReading -> {
            log.info("Publishing sensor reading {}", sensorReading);

            kafkaProducerService.publishReading(sensorReading);
        });
    }
}
