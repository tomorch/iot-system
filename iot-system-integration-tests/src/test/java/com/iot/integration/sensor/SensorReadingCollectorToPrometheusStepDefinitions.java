package com.iot.integration.sensor;

import com.iot.model.SensorReading;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SensorReadingCollectorToPrometheusStepDefinitions {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String readingId;
    private String sensorId;
    private String sensorType;
    private String groupId;
    private Double value;
    private Long timestamp;

    @Given("Sensor reading with readingId {string}, sensorId {string}, sensorType {string}, groupId {string} and value {double} published onto sensor reading topic")
    public void publishSensorReading(String readingId, String sensorId, String sensorType, String groupId, double value) {
        this.readingId = readingId;
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.groupId = groupId;
        this.value = value;
        this.timestamp = System.currentTimeMillis();

        System.out.println("timestamp: " + timestamp);

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:29092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        SensorReading sensorReading = new SensorReading(
            readingId,
            sensorId,
            sensorType,
            groupId,
            value,
            timestamp
        );

        try(Producer<String, String> producer = new KafkaProducer<>(props)) {
            String sensorReadingJsonString = objectMapper.writeValueAsString(sensorReading);

            ProducerRecord<String, String> record = new ProducerRecord<>("sensor-readings", sensorId, sensorReadingJsonString);

            producer.send(record);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("Querying Prometheus should return the corresponding sample")
    public void queryPrometheusSample() throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:9090/api/v1/query"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("query=sensor_value{reading_id=\"" + readingId + "\"}&time=" + dateFormat.format(Date.from(Instant.ofEpochMilli(timestamp)))))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String body = response.body();
        assertTrue(body.contains("\"reading_id\":\"" + readingId + "\""));
        assertTrue(body.contains("\"group_id\":\"" + groupId + "\""));
        assertTrue(body.contains("\"sensor_id\":\"" + sensorId + "\""));
        assertTrue(body.contains("\"sensor_type\":\"" + sensorType + "\""));
        assertTrue(body.contains(Double.toString(value)));
    }

}
