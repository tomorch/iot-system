package com.iot.sensor.integration;

import com.iot.sensor.model.SensorReading;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
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

import static com.iot.sensor.integration.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SensorQueryServiceStepDefinitions {
    private static final String TEST_PROPERTIES_FILENAME = "test.properties";

    private static final String SENSOR_VALUE_METRIC_NAME = "sensor_value";

    private final List<SensorReading> sensorReadings = new ArrayList<>();

    @Given("a reading with readingId {string}, sensorId {string}, sensorType {string}, groupId {string}, value {double} and current timestamp")
    public void createSensorReading(String readingId, String sensorId, String sensorType, String groupId, double value) {
        sensorReadings.add(new SensorReading(
                readingId,
                sensorId,
                sensorType,
                groupId,
                value,
                System.currentTimeMillis()
        ));
    }

    @When("corresponding samples are written to Prometheus")
    public void writeSensorReadingSamples() throws IOException, URISyntaxException, InterruptedException {
        if(sensorReadings.isEmpty()) {
            throw new RuntimeException("No sensor readings created - use \"Given a sensor reading...\" first");
        }

        Properties properties = PropertyLoader.loadProperties(TEST_PROPERTIES_FILENAME);

        HttpClient httpClient = HttpClient.newHttpClient();

        Remote.WriteRequest.Builder writeRequestBuilder = Remote.WriteRequest.newBuilder();

        for(SensorReading sensorReading : sensorReadings) {
            writeRequestBuilder.addTimeseries(createTimeSeries(sensorReading));
        }

        Remote.WriteRequest writeRequest =  writeRequestBuilder.build();

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

    @Then("sending a sensor query request should return an appropriate response")
    public void sendSensorQueryRequest() throws IOException, URISyntaxException, InterruptedException {
        if(sensorReadings.isEmpty()) {
            throw new RuntimeException("No sensor readings created - use \"Given a sensor reading...\" and \"When corresponding samples are written to Prometheus\" first");
        }

        Properties properties = PropertyLoader.loadProperties(TEST_PROPERTIES_FILENAME);

        HttpClient httpClient = HttpClient.newHttpClient();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        SensorReading first = sensorReadings.get(0);
        SensorReading last = sensorReadings.get(sensorReadings.size() - 1);

        String start = dateFormat.format(Date.from(Instant.ofEpochMilli(first.timestamp()-10))); // 10ms before
        String end = dateFormat.format(Date.from(Instant.ofEpochMilli(last.timestamp()+1000))); // 1s after

        HttpResponse<String> response = sendQueryServiceRequest(
            httpClient,
            properties.getProperty(IOT_SENSOR_QUERY_SERVICE_USERNAME_PROP),
            properties.getProperty(IOT_SENSOR_QUERY_SERVICE_PASSWORD_PROP),
            properties.getProperty(IOT_SENSOR_QUERY_SERVICE_BASE_URL_PROP),
            properties.getProperty(IOT_SENSOR_QUERY_SERVICE_SENSOR_PATH),
            String.format("%s?start=%s&end=%s", first.sensorId(), start, end)
        );

        String body = response.body();

        assertTrue(body.contains("\"groupId\":\"" + first.groupId() + "\""));
        assertTrue(body.contains("\"id\":\"" + first.sensorId() + "\""));
        assertTrue(body.contains("\"sensorType\":\"" + first.sensorType() + "\""));

        assertAggregatedValues(body);
    }

    @Then("sending a sensor group query request should return an appropriate response")
    public void sendSensorGroupQueryRequest() throws IOException, URISyntaxException, InterruptedException {
        if(sensorReadings.isEmpty()) {
            throw new RuntimeException("No sensor readings created - use \"Given a sensor reading...\" and \"When corresponding samples are written to Prometheus\" first");
        }

        Properties properties = PropertyLoader.loadProperties(TEST_PROPERTIES_FILENAME);

        HttpClient httpClient = HttpClient.newHttpClient();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        SensorReading first = sensorReadings.get(0);
        SensorReading last = sensorReadings.get(sensorReadings.size() - 1);

        String start = dateFormat.format(Date.from(Instant.ofEpochMilli(first.timestamp()-10))); // 10ms before
        String end = dateFormat.format(Date.from(Instant.ofEpochMilli(last.timestamp()+1000))); // 1s after

        HttpResponse<String> response = sendQueryServiceRequest(
            httpClient,
            properties.getProperty(IOT_SENSOR_QUERY_SERVICE_USERNAME_PROP),
            properties.getProperty(IOT_SENSOR_QUERY_SERVICE_PASSWORD_PROP),
            properties.getProperty(IOT_SENSOR_QUERY_SERVICE_BASE_URL_PROP),
            properties.getProperty(IOT_SENSOR_QUERY_SERVICE_SENSOR_GROUP_PATH),
            String.format("%s?start=%s&end=%s", first.groupId(), start, end)
        );

        String body = response.body();

        assertTrue(body.contains("\"groupId\":\"" + first.groupId() + "\""));

        assertAggregatedValues(body);
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

    private String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;

        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    private HttpResponse<String> sendQueryServiceRequest(HttpClient httpClient, String username, String password, String baseUrl, String path, String query) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(String.format(baseUrl + path + "/" + query)))
            .GET()
            .header("Authorization", getBasicAuthenticationHeader(username, password))
            .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void assertAggregatedValues(String body) {
        int count = sensorReadings.size();
        Double[] values = new Double[count];
        int index = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double total = 0;

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
}
