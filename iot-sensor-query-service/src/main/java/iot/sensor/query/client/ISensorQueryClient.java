package iot.sensor.query.client;

import iot.sensor.query.model.SensorQueryResult;

public interface ISensorQueryClient {
    public SensorQueryResult executeSensorQuery(String sensorId, String timeStart, String timeEnd, String time);
}
