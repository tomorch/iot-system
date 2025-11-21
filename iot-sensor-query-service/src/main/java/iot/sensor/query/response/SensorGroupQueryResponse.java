package iot.sensor.query.response;

public class SensorGroupQueryResponse extends AbstractSensorQueryResponse {
    private final SensorGroupQueryResult queryResult;

    public SensorGroupQueryResponse(SensorGroupQueryResult queryResult) {
        super(new Status.Success());

        this.queryResult = queryResult;
    }

    public SensorGroupQueryResult getQueryResult() {
        return queryResult;
    }
}
