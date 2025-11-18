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
            @RequestParam(name = "start") String start,
            @RequestParam(name = "end") String end) {

        SensorQueryResult sensorQueryResult = sensorQueryClient.executeSensorQuery(sensorId, start, end);

        return new SensorQueryResponse(sensorQueryResult);
    }
}
