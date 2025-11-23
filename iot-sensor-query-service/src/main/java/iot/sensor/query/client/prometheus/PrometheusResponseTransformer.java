package iot.sensor.query.client.prometheus;

import iot.sensor.query.client.prometheus.response.PrometheusQueryResponse;
import iot.sensor.query.client.prometheus.response.PrometheusResult;
import iot.sensor.query.response.SensorAggregatedReadingResult;
import iot.sensor.query.response.SensorGroupQueryResult;
import iot.sensor.query.response.SensorInfo;
import iot.sensor.query.response.SensorQueryResult;
import org.springframework.stereotype.Component;

import java.util.Arrays;
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

        return sensorInfo.map(info -> new SensorQueryResult(
                    info,
                    calculateAggregateResult(response.data().result())
                )
            )
            .orElseGet(() -> new SensorQueryResult(
                new SensorInfo(sensorId, null, null),
                new SensorAggregatedReadingResult(0, 0, 0, 0, 0)
            )
        );
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

        // iterate over the results populating the values array and adding to the total
        for(PrometheusResult result : results) {
            double value = Double.parseDouble(result.values()[0].value());

            if(value < min) min = value;
            if(value > max) max = value;

            total += value;

            values[index++] = value;
        }

        // sort the values in ascending order
        Arrays.sort(values);

        // calculate mean and median averages
        double mean = total / count;
        double median = count % 2 == 0 ?
                (values[count/2 - 1] + values[count/2]) / 2 :
                values[count/2];

        return new SensorAggregatedReadingResult(roundTo2DecimalPlaces(mean), roundTo2DecimalPlaces(median), min, max, count);
    }

    private double roundTo2DecimalPlaces(double input) {
        return Math.round(input * 100.0) / 100.0;
    }
}
