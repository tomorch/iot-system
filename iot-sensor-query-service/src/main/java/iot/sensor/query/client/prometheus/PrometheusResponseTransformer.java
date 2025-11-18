package iot.sensor.query.client.prometheus;

import iot.sensor.query.client.prometheus.response.PrometheusQueryResponse;
import iot.sensor.query.client.prometheus.response.PrometheusResult;
import iot.sensor.query.model.SensorInfo;
import iot.sensor.query.model.SensorQueryResult;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PrometheusResponseTransformer {
    SensorQueryResult transformToSensorQueryResult(PrometheusQueryResponse response) {
        Optional<SensorInfo> sensorInfo = response.data().result().stream()
                .findFirst().map(result ->
                        new SensorInfo(
                                result.metric().sensor_id(),
                                result.metric().sensor_type(), result.metric().group_id()));

        if(sensorInfo.isEmpty()) {
            return null;
        }

        long count = response.data().result().size();
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double total = 0;

        Double[] values = new Double[response.data().result().size()];
        int index = 0;

        for(PrometheusResult result : response.data().result()) {
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

        return new SensorQueryResult(sensorInfo.get(), mean, median, min, max, count);
    }
}
