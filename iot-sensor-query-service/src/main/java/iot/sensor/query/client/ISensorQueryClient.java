package iot.sensor.query.client;

import iot.sensor.query.response.SensorGroupQueryResult;
import iot.sensor.query.response.SensorQueryResult;

public interface ISensorQueryClient {
    SensorQueryResult executeSensorQuery(String sensorId, String start, String end);
    SensorGroupQueryResult executeSensorGroupQuery(String groupId, String start, String end);
}
