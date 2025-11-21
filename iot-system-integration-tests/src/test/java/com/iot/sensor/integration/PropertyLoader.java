package com.iot.sensor.integration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader {
    public static Properties loadProperties(String filename) throws IOException {
        Properties properties = new Properties();
        try (InputStream is = SensorReadingCollectorToPrometheusStepDefinitions.class.getResourceAsStream("/" + filename)) {
            properties.load(is);
        }
        return properties;
    }
}
