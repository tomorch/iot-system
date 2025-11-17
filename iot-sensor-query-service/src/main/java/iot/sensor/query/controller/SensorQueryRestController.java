package iot.sensor.query.controller;

import iot.sensor.query.model.DeviceQueryResponse;
import iot.sensor.query.model.QueryResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/sensor")
public class SensorQueryRestController {
    @GetMapping(path = "/query", produces = "application/json")
    public DeviceQueryResponse querySensorReadings(
            @RequestParam(required = false, name = "deviceId") String deviceId,
            @RequestParam(required = false, name = "sensorType") String sensorType,
            @RequestParam(required = false, name = "groupId") String groupId,
            @RequestParam(required = false, name = "readingId") String readingId,
            @RequestParam(required = false, name = "timeStart") String timeStart,
            @RequestParam(required = false, name = "timeEnd") String timeEnd,
            @RequestParam(required = false, name = "timeLast") String timeLast) {

        if(deviceId == null && sensorType == null && groupId == null && readingId == null) {
            throw new IllegalArgumentException("At least one of deviceId, sensorType, groupId, or readingId must be provided.");
        }

        if(timeStart == null && timeLast == null && timeEnd == null) {
            throw new IllegalArgumentException("At least one of timeStart, timeLast, or timeEnd must be provided.");
        }

        return new DeviceQueryResponse(new QueryResult(0, 0, 0, 0, 0));
    }
}
