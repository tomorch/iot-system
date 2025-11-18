package iot.sensor.query.controller;

import iot.sensor.query.client.ISensorQueryClient;
import iot.sensor.query.exception.BadRequestException;
import iot.sensor.query.model.SensorGroupQueryResponse;
import iot.sensor.query.model.SensorGroupQueryResult;
import iot.sensor.query.model.SensorQueryResponse;
import iot.sensor.query.model.SensorQueryResult;
import iot.sensor.query.utils.DateParamValidator;
import iot.sensor.query.utils.IdentifierParamValidator;
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

        if(!IdentifierParamValidator.validate(sensorId)) {
            throw new BadRequestException("sensorId is invalid - must contain no special characters");
        }

        if(!DateParamValidator.validate(start)) {
            throw new BadRequestException("start date is invalid - must conform to ISO 8601");
        }

        if(!DateParamValidator.validate(end)) {
            throw new BadRequestException("end date is invalid - must conform to ISO 8601");
        }

        SensorQueryResult sensorQueryResult = sensorQueryClient.executeSensorQuery(sensorId, start, end);

        return new SensorQueryResponse(sensorQueryResult);
    }

    @GetMapping(path = "/group/{groupId}", produces = "application/json")
    public SensorGroupQueryResponse querySensorGroupReadings(
            @PathVariable(name = "groupId") String groupId,
            @RequestParam(name = "start") String start,
            @RequestParam(name = "end") String end) {

        if(!IdentifierParamValidator.validate(groupId)) {
            throw new BadRequestException("groupId is invalid - must contain no special characters");
        }

        if(!DateParamValidator.validate(start)) {
            throw new BadRequestException("start date is invalid - must conform to ISO 8601");
        }

        if(!DateParamValidator.validate(end)) {
            throw new BadRequestException("end date is invalid - must conform to ISO 8601");
        }

        SensorGroupQueryResult sensorQueryResult = sensorQueryClient.executeSensorGroupQuery(groupId, start, end);

        return new SensorGroupQueryResponse(sensorQueryResult);
    }
}
