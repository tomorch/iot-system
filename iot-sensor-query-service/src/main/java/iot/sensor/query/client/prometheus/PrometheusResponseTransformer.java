package iot.sensor.query.client.prometheus;

import iot.sensor.query.client.prometheus.response.PrometheusQueryResponse;
import iot.sensor.query.client.prometheus.response.PrometheusResult;
import iot.sensor.query.model.SensorAggregatedReadingResult;
import iot.sensor.query.model.SensorGroupQueryResult;
import iot.sensor.query.model.SensorInfo;
import iot.sensor.query.model.SensorQueryResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PrometheusResponseTransformer {
    public SensorQueryResult transformToSensorQueryResult(String sensorId, PrometheusQueryResponse response) {
        Optional<SensorInfo> sensorInfo = response.data().result().stream()
                .findFirst().map(result ->
                        new SensorInfo(
                                result.metric().sensor_id(),
                                result.metric().sensor_type(), result.metric().group_id()));

        if(sensorInfo.isEmpty()) {
            return new SensorQueryResult(
                new SensorInfo(sensorId, null, null),
                new SensorAggregatedReadingResult(0, 0, 0, 0, 0)
            );
        }

        return new SensorQueryResult(sensorInfo.get(), calculateAggregateResult(response.data().result()));
    }

    public SensorGroupQueryResult transformToSensorGroupQueryResult(String groupId, PrometheusQueryResponse response) {
        if(response.data().result().isEmpty()) {
            return new SensorGroupQueryResult(groupId, new SensorAggregatedReadingResult(0, 0, 0, 0, 0));
        }

        return new SensorGroupQueryResult(groupId, calculateAggregateResult(response.data().result()));
    }

    private SensorAggregatedReadingResult calculateAggregateResult(List<PrometheusResult> results) {
        int count = results.size();
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double total = 0;

        Double[] values = new Double[count];
        int index = 0;

        for(PrometheusResult result : results) {
            double value = Double.parseDouble(result.values()[0].value());

            if(value < min) min = value;
            if(value > max) max = value;

            total += value;

            values[index++] = value;
        }

        double mean = total / count;
        double median = count % 2 == 0 ?
                (values[(int)count/2 - 1] + values[(int)count/2]) / 2 :
                values[(int)count/2];

        return new SensorAggregatedReadingResult(mean, median, min, max, count);
    }
}
