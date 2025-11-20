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
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SensorReadingCollectorToPrometheusStepDefinitions {
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Given("Sensor reading published onto sensor reading topic")
    public void publishSensorReading() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:29092");
        props.put("linger.ms", 1);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        SensorReading sensorReading = new SensorReading(
        "readingid",
        "sensorid",
        "sensortype",
        "groupid",
        5.5,
            System.currentTimeMillis()
        );

        try(Producer<String, String> producer = new KafkaProducer<>(props)) {
            String sensorReadingJsonString = objectMapper.writeValueAsString(sensorReading);

            ProducerRecord<String, String> record = new ProducerRecord<>("sensor-reading", "sensorid", sensorReadingJsonString);

            producer.send(record);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("Querying Prometheus should return the corresponding sample")
    public void queryPrometheusSample() throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:9090/api/v1/query"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("query=sensor_value{reading_id=\"readingid\"}"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

}
