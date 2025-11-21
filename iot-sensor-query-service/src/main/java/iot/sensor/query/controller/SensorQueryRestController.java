package iot.sensor.query.controller;

import iot.sensor.query.client.ISensorQueryClient;
import iot.sensor.query.response.*;
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
    public AbstractSensorQueryResponse querySensorReadings(
            @PathVariable(name = "sensorId") String sensorId,
            @RequestParam(name = "start") String start,
            @RequestParam(name = "end") String end) {

        if(!IdentifierParamValidator.validate(sensorId)) {
            return new FailureSensorQueryResponse("sensorId is invalid - must contain no special characters");
        }

        if(!DateParamValidator.validate(start)) {
            return new FailureSensorQueryResponse("start date is invalid - must conform to ISO 8601");
        }

        if(!DateParamValidator.validate(end)) {
            return new FailureSensorQueryResponse("end date is invalid - must conform to ISO 8601");
        }

        SensorQueryResult sensorQueryResult = sensorQueryClient.executeSensorQuery(sensorId, start, end);

        if(sensorQueryResult == null) {
            return new FailureSensorQueryResponse("failed to execute sensor query with remote service");
        }

        return new SensorQueryResponse(sensorQueryResult);
    }

    @GetMapping(path = "/group/{groupId}", produces = "application/json")
    public AbstractSensorQueryResponse querySensorGroupReadings(
            @PathVariable(name = "groupId") String groupId,
            @RequestParam(name = "start") String start,
            @RequestParam(name = "end") String end) {

        if(!IdentifierParamValidator.validate(groupId)) {
            return new FailureSensorQueryResponse("groupId is invalid - must contain no special characters");
        }

        if(!DateParamValidator.validate(start)) {
            return new FailureSensorQueryResponse("start date is invalid - must conform to ISO 8601");
        }

        if(!DateParamValidator.validate(end)) {
            return new FailureSensorQueryResponse("end date is invalid - must conform to ISO 8601");
        }

        SensorGroupQueryResult sensorQueryResult = sensorQueryClient.executeSensorGroupQuery(groupId, start, end);

        if(sensorQueryResult == null) {
            return new FailureSensorQueryResponse("failed to execute sensor query with remote service");
        }

        return new SensorGroupQueryResponse(sensorQueryResult);
    }
}
