package iot.sensor.query.controller;

import iot.sensor.query.client.ISensorQueryClient;
import iot.sensor.query.exception.BadRequestException;
import iot.sensor.query.model.SensorQueryResponse;
import iot.sensor.query.model.SensorQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/sensor")
public class SensorQueryRestController {
    @Autowired
    private ISensorQueryClient sensorQueryClient;

    @GetMapping(path = "/{sensorId}", produces = "application/json")
    public SensorQueryResponse querySensorReadings(
            @PathVariable(name = "sensorId") String sensorId,
            @RequestParam(required = false, name = "timeStart") String timeStart,
            @RequestParam(required = false, name = "timeEnd") String timeEnd,
            @RequestParam(required = false, name = "time") String time) {

        if(timeStart == null && time == null && timeEnd == null) {
            throw new BadRequestException("At least one of timeStart, timeLast, or timeEnd must be provided.");
        }

        SensorQueryResult sensorQueryResult = sensorQueryClient.executeSensorQuery(sensorId, timeStart, timeEnd, time);

        return new SensorQueryResponse(sensorQueryResult);
    }
}
