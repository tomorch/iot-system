package com.iot.simulator.scheduler;

import com.iot.simulator.generator.SensorReadingGenerator;
import com.iot.simulator.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.jmx.export.annotation.ManagedResource;

@Component
@ManagedResource
public class SensorSimulationScheduler {
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private SensorReadingGenerator sensorReadingGenerator;

    @Scheduled(fixedRate = 1000)
    public void publishSensorReadings() {
        sensorReadingGenerator.generateReadings().forEach(sensorReading -> {
            kafkaProducerService.publishReading(sensorReading);
        });
    }
}
