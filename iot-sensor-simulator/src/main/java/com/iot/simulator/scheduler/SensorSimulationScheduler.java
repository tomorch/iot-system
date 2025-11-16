package com.iot.simulator.scheduler;

import com.iot.model.SensorReading;
import com.iot.simulator.config.DeviceConfig;
import com.iot.simulator.config.SensorGroupConfig;
import com.iot.simulator.config.SimulatorConfig;
import com.iot.simulator.generator.ISensorReadingGenerator;
import com.iot.simulator.service.KafkaProducerService;
import com.iot.simulator.service.SensorGroupsReadingGeneratorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.Arrays;
import java.util.List;

import static com.iot.simulator.Constants.*;

@Component
@ManagedResource
public class SensorSimulationScheduler {

    @Autowired
    private SensorGroupsReadingGeneratorProvider readingGeneratorProvider;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    private SimulatorConfig simulatorConfig = loadSimulatorConfig();

    @Scheduled(fixedRate = 1000)
    public void publishSensorReadings() {
        simulatorConfig.sensorGroupConfigs().stream().forEach(sensorGroupConfig -> {
            ISensorReadingGenerator sensorReadingGenerator =
                    readingGeneratorProvider.getSensorReadingGeneratorBySensorType(sensorGroupConfig.type());

            sensorGroupConfig.deviceConfigs().stream().forEach(deviceConfig -> {
                SensorReading sensorReading = sensorReadingGenerator.generateSensorReading(sensorGroupConfig, deviceConfig);

                if (sensorReading != null) {
                    kafkaProducerService.publishReading(sensorReading, sensorGroupConfig.topic());
                }
            });
        });
    }

    private SimulatorConfig loadSimulatorConfig() {
        List<DeviceConfig> thermostats = Arrays.asList(
                new DeviceConfig("therm01", "home", "Living Room"),
                new DeviceConfig("therm02", "home", "Master Bedroom"),
                new DeviceConfig("therm03", "work", "Office"),
                new DeviceConfig("therm04", "work", "Kitchen")
        );

        List<DeviceConfig> heartRateMonitors = Arrays.asList(
                new DeviceConfig("hm01", "fitness", "Alice"),
                new DeviceConfig("hm02", "fitness", "John"),
                new DeviceConfig("hm03", "medical", "Fred")
        );

        List<DeviceConfig> fuelGauges = Arrays.asList(
                new DeviceConfig("fg01", "personal", "Tim's Car"),
                new DeviceConfig("fg02", "personal", "Joan's Car"),
                new DeviceConfig("fg03", "work", "Joan's Van")
        );

        List<SensorGroupConfig> sensorGroupConfigs = Arrays.asList(
                new SensorGroupConfig(SENSOR_TYPE_THERMOSTAT, "sensor-readings", thermostats),
                new SensorGroupConfig(SENSOR_TYPE_HEART_RATE_MONITOR, "sensor-readings", heartRateMonitors),
                new SensorGroupConfig(SENSOR_TYPE_FUEL_GAUGE, "sensor-readings", fuelGauges)
        );

        return new SimulatorConfig(1000L, sensorGroupConfigs);
    }
}
