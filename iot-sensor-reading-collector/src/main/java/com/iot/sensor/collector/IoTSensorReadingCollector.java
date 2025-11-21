package com.iot.sensor.collector;

import com.iot.sensor.collector.topology.SensorReadingToPrometheusTimeSeriesMapper;
import com.iot.sensor.model.SensorReading;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.prometheus.sink.PrometheusSink;
import org.apache.flink.formats.json.JsonDeserializationSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class IoTSensorReadingCollector {
    private static final Logger log = LoggerFactory.getLogger(IoTSensorReadingCollector.class);

    private static final String APPLICATION_PROPERTIES_FILENAME = "application.properties";

    private static final String KAFKA_BOOTSTRAP_SERVERS_PROP = "kafka.bootstrap-servers";
    private static final String KAFKA_TOPICS_PROP = "kafka.topics";
    private static final String KAFKA_CONSUMER_GROUP_PROP = "kafka.consumer-group";

    private static final String PROMETHEUS_WRITE_URL_PROP = "prometheus.write.url";

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try (InputStream is = IoTSensorReadingCollector.class.getResourceAsStream("/" + APPLICATION_PROPERTIES_FILENAME)) {
            properties.load(is);
        }

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
