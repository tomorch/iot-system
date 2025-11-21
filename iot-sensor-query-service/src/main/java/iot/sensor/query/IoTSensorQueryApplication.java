package iot.sensor.query;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class IoTSensorQueryApplication {
    public static void main(String... args) {
        SpringApplication.run(IoTSensorQueryApplication.class, args);
    }
}