package com.iot.sensor.collector;

import com.iot.sensor.collector.topology.SensorReadingToPrometheusTimeSeriesMapper;
import com.iot.sensor.model.SensorReading;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.prometheus.sink.PrometheusSink;
import org.apache.flink.formats.json.JsonDeserializationSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.InputStream;
import java.util.Properties;

import static com.iot.sensor.collector.Constants.*;

public class IoTSensorReadingCollector {
    public static void main(String[] args) throws Exception {
        // load property file
        Properties properties = new Properties();
        try (InputStream is = IoTSensorReadingCollector.class.getResourceAsStream("/" + APPLICATION_PROPERTIES_FILENAME)) {
            properties.load(is);
        }

        // initialise the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // create the kafka source
        JsonDeserializationSchema<SensorReading> jsonDeserializationSchema =
                new JsonDeserializationSchema<>(SensorReading.class);

        KafkaSource<SensorReading> source = KafkaSource.<SensorReading>builder()
                .setBootstrapServers(properties.getProperty(KAFKA_BOOTSTRAP_SERVERS_PROP))
                .setTopics(properties.getProperty(KAFKA_TOPICS_PROP))
                .setGroupId(properties.getProperty(KAFKA_CONSUMER_GROUP_PROP))
                .setValueOnlyDeserializer(jsonDeserializationSchema)
                .build();

        // create the prometheus sink
        PrometheusSink sink = (PrometheusSink) PrometheusSink.builder()
                .setPrometheusRemoteWriteUrl(properties.getProperty(PROMETHEUS_WRITE_URL_PROP))
                .build();

        // define the topology
        env.fromSource(source, WatermarkStrategy.noWatermarks(), "Sensor Reading Source")
            .keyBy(SensorReading::sensorId)
            .map(new SensorReadingToPrometheusTimeSeriesMapper())
            .sinkTo(sink);

        // go
        env.execute("IoT Sensor Reading Collector");
    }
}
