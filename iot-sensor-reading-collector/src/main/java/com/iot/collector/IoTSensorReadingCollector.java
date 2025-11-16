package com.iot.collector;

import com.iot.model.SensorReading;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.formats.json.JsonDeserializationSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

public class IoTSensorReadingCollector {
    static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        JsonDeserializationSchema<SensorReading> jsonDeserializationSchema =
                new JsonDeserializationSchema<>(SensorReading.class);

        KafkaSource<SensorReading> source = KafkaSource.<SensorReading>builder()
                .setBootstrapServers("localhost:29092")
                .setTopics("sensor-readings")
                .setGroupId("sensor-reading-collector-group")
                .setValueOnlyDeserializer(jsonDeserializationSchema)
                .build();

        env.fromSource(source, WatermarkStrategy.noWatermarks(), "Sensor Reading Source")
            .process(new ProcessFunction<SensorReading, SensorReading>() {
                @Override
                public void processElement(SensorReading reading, Context ctx, Collector<SensorReading> out) {
                    System.out.println("Received Sensor Reading: " + reading.toString());
                    out.collect(reading); // forward if needed
                }
            });

        env.execute("IoT Sensor Reading Collector");
    }

}
