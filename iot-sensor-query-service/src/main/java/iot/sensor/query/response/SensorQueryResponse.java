package iot.sensor.query.response;

public class SensorQueryResponse extends AbstractSensorQueryResponse {
    private final SensorQueryResult queryResult;

    public SensorQueryResponse(SensorQueryResult queryResult) {
        super(new Status.Success());

        this.queryResult = queryResult;
    }

    public SensorQueryResult getQueryResult() {
        return queryResult;
    }
}