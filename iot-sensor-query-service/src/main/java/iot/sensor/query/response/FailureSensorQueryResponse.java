package iot.sensor.query.response;

public class FailureSensorQueryResponse extends AbstractSensorQueryResponse {
    public FailureSensorQueryResponse(String failureReason) {
        super(new Status.Failure(failureReason));
    }
}
