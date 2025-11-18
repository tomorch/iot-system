package iot.sensor.query.client;

import iot.sensor.query.model.SensorQueryResult;

public interface ISensorQueryClient {
    SensorQueryResult executeSensorQuery(String sensorId, String start, String end);
}
