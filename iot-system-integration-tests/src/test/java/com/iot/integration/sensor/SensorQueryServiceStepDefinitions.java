package com.iot.integration.sensor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.model.SensorReading;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.commons.codec.digest.DigestUtils;
import org.xerial.snappy.Snappy;
import prometheus.Remote;
import prometheus.Types;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

import static com.iot.integration.sensor.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SensorQueryServiceStepDefinitions {
    private static final String TEST_PROPERTIES_FILENAME = "test.properties";

    private static final String SENSOR_VALUE_METRIC_NAME = "sensor_value";

    private List<SensorReading> sensorReadings = new ArrayList<>();

    @Given("Sensor reading sample with readingId {string}, sensorId {string}, sensorType {string}, groupId {string} and value {double} written to Prometheus")
    public void writeSensorReadingSample(String readingId, String sensorId, String sensorType, String groupId, double value) throws IOException, URISyntaxException, InterruptedException {
        Properties properties = PropertyLoader.loadProperties(TEST_PROPERTIES_FILENAME);

        HttpClient httpClient = HttpClient.newHttpClient();

        SensorReading sensorReading = new SensorReading(
                readingId,
                sensorId,
                sensorType,
                groupId,
                value,
                System.currentTimeMillis()
        );

        sensorReadings.add(sensorReading);

        Remote.WriteRequest writeRequest = Remote.WriteRequest.newBuilder().addTimeseries(createTimeSeries(sensorReading)).build();

        byte[] compressed = Snappy.compress(writeRequest.toByteArray());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(properties.getProperty(PROMETHEUS_BASE_URL_PROP) + properties.getProperty(PROMETHEUS_WRITE_PATH_PROP)))
                .header("Content-Type", "application/x-protobuf")
                .header("Content-Encoding", "snappy")
                .header("X-Prometheus-Remote-Write-Version", "0.1.0")
                .POST(HttpRequest.BodyPublishers.ofByteArray(compressed))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(204, response.statusCode());
    }

    @Then("Sending a sensor query request should return an appropriate response")
    public void sendSensorQueryRequest() throws IOException, URISyntaxException, InterruptedException {
        Properties properties = PropertyLoader.loadProperties(TEST_PROPERTIES_FILENAME);

        HttpClient httpClient = HttpClient.newHttpClient();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        SensorReading first = sensorReadings.get(0);
        SensorReading last = sensorReadings.get(sensorReadings.size() - 1);

        String start = dateFormat.format(Date.from(Instant.ofEpochMilli(first.timestamp()-10))); // 10ms before
        String end = dateFormat.format(Date.from(Instant.ofEpochMilli(last.timestamp()+1000))); // 1s after

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(String.format(
                        properties.getProperty(IOT_SENSOR_QUERY_SERVICE_BASE_URL_PROP) +
                        properties.getProperty(IOT_SENSOR_QUERY_SERVICE_SENSOR_PATH) +
                        "%s?start=%s&end=%s", first.sensorId(), start, end)))
                .GET()
                .header("Authorization", getBasicAuthenticationHeader("user", "password"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        String body = response.body();

        int count = sensorReadings.size();
        Double[] values = new Double[count];
        int index = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double total = 0;

        assertTrue(body.contains("\"groupId\":\"" + first.groupId() + "\""));
        assertTrue(body.contains("\"id\":\"" + first.sensorId() + "\""));
        assertTrue(body.contains("\"sensorType\":\"" + first.sensorType() + "\""));

        for(SensorReading sensorReading : sensorReadings) {
            double value = sensorReading.value();

            if(value < min) min = value;
            if(value > max) max = value;

            total += value;

            values[index++] = value;
        }

        double mean = total / count;

        double median = count % 2 == 0 ?
                (values[count/2 - 1] + values[count/2]) / 2 :
                values[count/2];

        assertTrue(body.contains("\"mean\":" + roundDoubleTo2DecimalPlaces(mean)));
        assertTrue(body.contains("\"median\":" + roundDoubleTo2DecimalPlaces(median)));
        assertTrue(body.contains("\"min\":" + min));
        assertTrue(body.contains("\"max\":" + max));
        assertTrue(body.contains("\"count\":" + count));
    }

    private double roundDoubleTo2DecimalPlaces(double input) {
        return Math.round(input * 100.0) / 100.0;
    }

    private Types.TimeSeries createTimeSeries(SensorReading sensorReading) {
        return Types.TimeSeries.newBuilder()
            .addLabels(Types.Label.newBuilder().setName("__name__").setValue(SENSOR_VALUE_METRIC_NAME).build())
            .addLabels(Types.Label.newBuilder().setName("reading_id").setValue(sensorReading.id()).build())
            .addLabels(Types.Label.newBuilder().setName("sensor_id").setValue(sensorReading.sensorId()).build())
            .addLabels(Types.Label.newBuilder().setName("sensor_type").setValue(sensorReading.sensorType()).build())
            .addLabels(Types.Label.newBuilder().setName("group_id").setValue(sensorReading.groupId()).build())
            .addSamples(Types.Sample.newBuilder().setValue(sensorReading.value()).setTimestamp(sensorReading.timestamp()).build())
            .build();
    }

    private static String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;

        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
}
