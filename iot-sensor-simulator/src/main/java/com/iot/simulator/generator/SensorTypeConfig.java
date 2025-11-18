package com.iot.simulator.generator;

import java.util.List;

public class SensorTypeConfig {
    private String sensorType;
    private String sensorIdPrefix;
    private int numSensors;
    private double minValue;
    private double maxValue;
    private List<String> groupIds;
    private String topic;

    public int getNumSensors() {
        return numSensors;
    }

    public void setNumSensors(int numSensors) {
        this.numSensors = numSensors;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getSensorIdPrefix() {
        return sensorIdPrefix;
    }

    public void setSensorIdPrefix(String sensorIdPrefix) {
        this.sensorIdPrefix = sensorIdPrefix;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
