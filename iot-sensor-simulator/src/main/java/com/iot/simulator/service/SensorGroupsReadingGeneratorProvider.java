package com.iot.simulator.service;

import com.iot.simulator.generator.FuelGaugeReadingGenerator;
import com.iot.simulator.generator.HeartRateReadingGenerator;
import com.iot.simulator.generator.ISensorReadingGenerator;
import com.iot.simulator.generator.ThermostatReadingGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.iot.simulator.Constants.*;

@Service
public class SensorGroupsReadingGeneratorProvider {
    @Autowired
    private ThermostatReadingGenerator thermostatReadingGenerator;

    @Autowired
    private HeartRateReadingGenerator heartRateReadingGenerator;

    @Autowired
    private FuelGaugeReadingGenerator fuelGaugeReadingGenerator;

    public ISensorReadingGenerator getSensorReadingGeneratorBySensorType(String sensorType) {
        switch (sensorType) {
            case SENSOR_TYPE_THERMOSTAT:
                return thermostatReadingGenerator;
            case SENSOR_TYPE_HEART_RATE_MONITOR:
                return heartRateReadingGenerator;
            case SENSOR_TYPE_FUEL_GAUGE:
                return fuelGaugeReadingGenerator;
            default:
                throw new IllegalArgumentException("unrecognised sensor type: " + sensorType);
        }
    }
}
