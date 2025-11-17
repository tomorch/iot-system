package com.iot.collector;

import com.iot.model.SensorReading;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.prometheus.sink.PrometheusSink;
import org.apache.flink.connector.prometheus.sink.PrometheusTimeSeries;
import org.apache.flink.formats.json.JsonDeserializationSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IoTSensorReadingCollector {
    private static final Logger log = LoggerFactory.getLogger(IoTSensorReadingCollector.class);

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        JsonDeserializationSchema<SensorReading> jsonDeserializationSchema =
                new JsonDeserializationSchema<>(SensorReading.class);

        PrometheusSink sink = (PrometheusSink) PrometheusSink.builder()
                .setPrometheusRemoteWriteUrl("http://localhost:9090/api/v1/write")
                .build();

        KafkaSource<SensorReading> source = KafkaSource.<SensorReading>builder()
                .setBootstrapServers("localhost:29092")
                .setTopics("sensor-readings")
                .setGroupId("sensor-reading-collector-group")
                .setValueOnlyDeserializer(jsonDeserializationSchema)
                .build();

        env.fromSource(source, WatermarkStrategy.noWatermarks(), "Sensor Reading Source")
            .keyBy(SensorReading::deviceId)
            .process(new ProcessFunction<SensorReading, PrometheusTimeSeries>() {
                @Override
                public void processElement(SensorReading sensorReading, ProcessFunction<SensorReading, PrometheusTimeSeries>.Context context, Collector<PrometheusTimeSeries> collector) {
                    log.info("received reading: {}", sensorReading);
                    PrometheusTimeSeries timeSeries = PrometheusTimeSeries.builder()
                        .withMetricName("sensor_value")
                        .addLabel("device_id", sensorReading.deviceId())
                        .addLabel("sensor_type", sensorReading.sensorType())
                        .addLabel("group_id", sensorReading.groupId())
                        .addLabel("reading_id", sensorReading.id())
                        .addSample(sensorReading.value(), sensorReading.timestamp())
                        .build();

                    collector.collect(timeSeries);
                }
            })
            .sinkTo(sink);

        env.execute("IoT Sensor Reading Collector");
    }

}
