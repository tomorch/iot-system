package iot.sensor.query.model;

public record DeviceQueryRequest(String deviceId, String sensorType, String groupId, String readingId, String timeStart, String timeEnd, String timeLast) { }
