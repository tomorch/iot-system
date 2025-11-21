package com.iot.simulator.generator;

import com.iot.model.SensorReading;

public record SensorGeneratedReading(SensorReading sensorReading, String topic) { }