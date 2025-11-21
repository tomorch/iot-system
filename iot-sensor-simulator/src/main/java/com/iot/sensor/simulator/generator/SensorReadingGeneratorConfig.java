package com.iot.simulator.generator;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "sensor.simulator")
public class SensorReadingGeneratorConfig {
    private List<SensorTypeConfig> sensorTypeConfigs;

    public List<SensorTypeConfig> getSensorTypeConfigs() {
        return sensorTypeConfigs;
    }

    public void setSensorTypeConfigs(List<SensorTypeConfig> sensorTypeConfigs) {
        this.sensorTypeConfigs = sensorTypeConfigs;
    }
}
