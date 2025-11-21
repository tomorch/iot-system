package com.iot.sensor.simulator.generator;

import com.iot.sensor.model.SensorReading;

public record SensorGeneratedReading(SensorReading sensorReading, String topic) { }