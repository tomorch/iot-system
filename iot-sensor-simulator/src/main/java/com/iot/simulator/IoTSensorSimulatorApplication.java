package com.iot.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IoTSensorSimulatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(IoTSensorSimulatorApplication.class, args);
    }

}
