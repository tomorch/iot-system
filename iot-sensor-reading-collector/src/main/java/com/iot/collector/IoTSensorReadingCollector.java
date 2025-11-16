package com.iot.collector;

import org.apache.flink.datastream.api.ExecutionEnvironment;
import org.apache.flink.formats.json.JsonDeserializationSchema;

public class IoTSensorReadingCollector {
    static void main(String[] args) throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getInstance();

        //JsonDeserializationSchema jsonDeserializationSchema = new JsonDeserializationSchema();
    }
}
